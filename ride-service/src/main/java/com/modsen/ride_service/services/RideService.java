package com.modsen.ride_service.services;

import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.exceptions.ErrorServiceResponseException;
import com.modsen.ride_service.exceptions.InvalidRideStatusException;
import com.modsen.ride_service.exceptions.RideNotFoundException;
import com.modsen.ride_service.feign_clients.*;
import com.modsen.ride_service.mappers.ride_mappers.RideDTOMapper;
import com.modsen.ride_service.mappers.ride_mappers.RideMapper;
import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.dtos.PassengerNotificationDTO;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.dtos.RidePatchDTO;
import com.modsen.ride_service.models.entitties.Ride;
import com.modsen.ride_service.repositories.RideRepository;
import constants.KafkaConstants;
import enums.DriverStatus;
import lombok.RequiredArgsConstructor;
import models.dtos.GetFreeDriverNotInListRequest;
import models.dtos.PaymentDTO;
import models.dtos.RideInfo;
import models.dtos.events.ChangeDriverStatusEvent;
import models.dtos.events.MakePaymentOnCompleteEvent;
import models.dtos.requests.ChangeDriverStatusRequest;
import models.dtos.responses.FreeDriver;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.CalculationUtil;
import utils.PatchUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RideService {

    private List<UUID> idExclusions = new ArrayList<>();

    private final BingMapsService mapsService;
    private final DriverNotificationService driverNotificationService;
    private final PassengerNotificationService passengerNotificationService;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DriverFeignClient driverFeignClient;
    private final PaymentFeignClient paymentFeignClient;
    private final DriverRatingFeignClient driverRatingFeignClient;
    private final PassengerRatingFeignClient passengerRatingFeignClient;
    private final PassengerBankAccountFeignClient passengerBankAccountFeignClient;

    private final RideMapper rideMapper;
    private final RideDTOMapper rideDTOMapper;
    private final RideRepository repository;

    @Transactional
    public RideDTO createRide(RideDTO rideDTO) {
        fillInRideOnCreation(rideDTO);

        Ride ride = rideDTOMapper.toRide(rideDTO);
        Ride savedRide = repository.save(ride);

        sendNotification(savedRide);

        return rideMapper.toRideDTO(savedRide);
    }

    @Transactional(readOnly = true)
    public RideDTO getRideById(UUID id) {
        Ride ride = repository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride entity with id='%s' not found".formatted(id)));
        return rideMapper.toRideDTO(ride);
    }

    public GetAllPaginatedResponse<RideDTO> getPaginatedRidesByPassengerId(
            UUID passengerId,
            PageRequest pageRequest
    ) {
        Page<Ride> ridePage = repository.findByPassengerId(passengerId, pageRequest);

        return getAllPaginatedResponseDTO(ridePage);
    }

    public GetAllPaginatedResponse<RideDTO> getPaginatedPassengerRidesInDateRange(
            UUID passengerId,
            LocalDateTime from,
            LocalDateTime to,
            PageRequest pageRequest
    ) {
        Page<Ride> ridePage = repository.findByPassengerIdAndCreatedAtIsBetween(passengerId, from, to, pageRequest);

        return getAllPaginatedResponseDTO(ridePage);
    }

    public GetAllPaginatedResponse<RideDTO> getPaginatedDriverRidesInDateRange(
            UUID driverId,
            LocalDateTime from,
            LocalDateTime to,
            PageRequest pageRequest
    ) {
        Page<Ride> ridePage = repository.findByDriverIdAndCreatedAtIsBetween(driverId, from, to, pageRequest);

        return getAllPaginatedResponseDTO(ridePage);
    }

    public GetAllPaginatedResponse<RideDTO> getPaginatedRidesByDriverId(UUID driverId, PageRequest pageRequest) {
        Page<Ride> ridePage = repository.findByDriverId(driverId, pageRequest);

        return getAllPaginatedResponseDTO(ridePage);
    }

    public RideDTO updateRide(UUID rideId, RideDTO rideDTO) {
        Ride ride = repository.findByRideId(rideId);
        fillInRideOnUpdate(ride, rideDTO);

        return rideMapper.toRideDTO(repository.save(ride));
    }

    public RideDTO patchRide(UUID rideId, RidePatchDTO ridePatchDTO) {
        Ride ride = repository.findByRideId(rideId);
        fillInRideOnPatch(ride, ridePatchDTO);

        return rideMapper.toRideDTO(repository.save(ride));
    }

    @Transactional
    public RideDTO changeRideStatus(UUID id, RideStatus rideStatus) {
        Ride ride = repository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride entity with id='%s' cannot be found"
                        .formatted(id)));

        if(!rideStatus.canBeObtainedFrom().contains(ride.getStatus())) {
            throw new InvalidRideStatusException("Status '%s' cannot be set".formatted(rideStatus));
        }

        handleRideStatus(ride, rideStatus);

        return rideMapper.toRideDTO(repository.save(ride));
    }

    public void recoverRide(UUID rideId) {
        Ride ride = repository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride entity with id='%s' cannot be found"
                        .formatted(rideId)));
        ride.setStatus(RideStatus.IN_RIDE);
        ride.setEndTime(null);

        repository.save(ride);
    }

    @Transactional
    public RideDTO approveDriverRequestByRideIdAndDriverId(UUID rideId, UUID driverId) {
        driverFeignClient.changeDriverStatus(driverId, new ChangeDriverStatusRequest(DriverStatus.BUSY));

        RideDTO rideDTO = changeRideStatus(rideId, RideStatus.ACCEPTED);

        passengerNotificationService.createPassengerNotification(PassengerNotificationDTO.builder()
                .passengerId(rideDTO.getPassengerId())
                .message("Driver accepted ride request")
                .driverRating(Objects.requireNonNull(
                                driverRatingFeignClient.getDriverRatingStatistic(driverId.toString()).getBody())
                        .getRating())
                .build());

        PaymentDTO paymentDTO = PaymentDTO.builder()
                .rideId(rideDTO.getId().toString())
                .passengerId(rideDTO.getPassengerId().toString())
                .driverId(driverId.toString())
                .promoCode(rideDTO.getPromoCode())
                .cost(rideDTO.getCost())
                .rideInfo(new RideInfo(
                        rideDTO.getPaymentMethod(),
                        rideDTO.getCarCategory(),
                        rideDTO.getDistance()
                ))
                .build();

        paymentFeignClient.createPayment(paymentDTO);

        rideDTO.setDriverId(driverId);

        Ride savedRide = repository.save(rideDTOMapper.toRide(rideDTO));
        driverNotificationService.changeStatusOnReadByRideIdAndDriverId(rideId, driverId);

        return rideMapper.toRideDTO(savedRide);
    }

    @Transactional
    public RideDTO rejectDriverRequestByRideId(UUID rideId, UUID driverId) {
        paymentFeignClient.deletePayment(rideId.toString());
        driverFeignClient.changeDriverStatus(driverId, new ChangeDriverStatusRequest(DriverStatus.FREE));

        driverNotificationService.changeStatusOnReadByRideIdAndDriverId(rideId, driverId);

        Ride ride = repository.findByRideId(rideId);
        ride.setDriverId(null);

        return sendNotification(repository.save(ride));
    }

    private RideDTO sendNotification(Ride ride) {
        UUID freeDriverId = Optional.ofNullable(
                        driverFeignClient.getFreeDriverNotInList(new GetFreeDriverNotInListRequest(
                                idExclusions,
                                ride.getSeatsCount(),
                                ride.getCarCategory()
                        ))
                )
                .map(ResponseEntity::getBody)
                .map(FreeDriver::getDriverId)
                .orElseThrow(() -> new ErrorServiceResponseException("No available drivers at the moment"));

        // add found driver id to exclusion to not send same ride notification again
        idExclusions.add(freeDriverId);

        sendDriverNotification(ride, freeDriverId);

        return rideMapper.toRideDTO(ride);
    }

    private void sendDriverNotification(Ride ride, UUID driverId) {
        DriverNotificationDTO notificationDTO = DriverNotificationDTO.builder()
                .rideId(ride.getId())
                .driverId(driverId)
                .passengerRating(
                        Objects.requireNonNull(passengerRatingFeignClient
                                .getPassengerRatingStatistic(ride.getPassengerId().toString()).getBody())
                                .getRating()
                )
                .build();

        driverNotificationService.createDriverNotification(notificationDTO);
    }

    private GetAllPaginatedResponse<RideDTO> getAllPaginatedResponseDTO(Page<Ride> ridePage) {
        List<RideDTO> rideDTOs = ridePage.stream()
                .map(rideMapper::toRideDTO)
                .toList();

        return new GetAllPaginatedResponse<>(
                rideDTOs,
                ridePage.getTotalPages(),
                ridePage.getTotalElements()
        );
    }

    private void handleRideStatus(Ride ride, RideStatus rideStatus) {
        switch(rideStatus) {
            case IN_RIDE -> handleInRideStatus(ride);
            case COMPLETED -> handleCompletedRideStatus(ride);
            case REQUESTED -> handleRequestedStatus(ride);
        }
        ride.setStatus(rideStatus);
    }

    private void handleCompletedRideStatus(Ride ride) {
        kafkaTemplate.send(KafkaConstants.RIDE_COMPLETED_EVENT, new ChangeDriverStatusEvent(
                ride.getDriverId(),
                DriverStatus.FREE,
                ride.getId())
        );
        kafkaTemplate.send(KafkaConstants.RIDE_PAYMENT_ON_COMPLETE_EVENT, new MakePaymentOnCompleteEvent(
                ride.getId().toString()
        ));
        ride.setEndTime(LocalDateTime.now());
    }

    private void handleInRideStatus(Ride ride) {
        ride.setStartTime(LocalDateTime.now());
    }

    private void handleRequestedStatus(Ride ride) {
        ride.setDriverId(null);
    }

    private void fillInRideOnCreation(RideDTO rideDTO) {
        Map<String, String> rideDetails = mapsService.getRideDetails(rideDTO);


        double distance = Double.parseDouble(rideDetails.get("distance"));

        BigDecimal cost = CalculationUtil.calculateRideCostByDistanceAndCarCategoryAndPromoCode(
                distance,
                rideDTO.getCarCategory(),
                rideDTO.getPromoCode()
        );

        checkPassengerBalance(rideDTO.getPassengerId().toString(), cost);

        rideDTO.setCost(cost);
        rideDTO.setDistance(distance);
        rideDTO.setOriginAddress(rideDetails.get("originAddress"));
        rideDTO.setDestinationAddress(rideDetails.get("destinationAddress"));
        rideDTO.setStatus(RideStatus.REQUESTED);
        rideDTO.setCreatedAt(LocalDateTime.now());
    }

    private void checkPassengerBalance(String passengerId, BigDecimal cost) {
        BigDecimal passengerBalance = Objects.requireNonNull(
                passengerBankAccountFeignClient.getBalance(passengerId).getBody()
        ).getBalance();
        if(passengerBalance.compareTo(cost) < 0) {
            throw new ErrorServiceResponseException("Insufficient passenger balance");
        }
    }

    // TODO: move fillIns to utils/dto (?)
    private static void fillInRideOnUpdate(Ride ride, RideDTO rideDTO) {
        double originLatitude = rideDTO.getOriginLatitude();
        double originLongitude = rideDTO.getOriginLongitude();
        double destinationLatitude = rideDTO.getDestinationLatitude();
        double destinationLongitude = rideDTO.getDestinationLongitude();

        ride.setOriginLatitude(originLatitude);
        ride.setOriginLongitude(originLongitude);
        ride.setDestinationLatitude(destinationLatitude);
        ride.setDestinationLongitude(destinationLongitude);
        ride.setPassengerId(rideDTO.getPassengerId());
        ride.setSeatsCount(rideDTO.getSeatsCount());
        ride.setCarCategory(rideDTO.getCarCategory());
        ride.setPaymentMethod(rideDTO.getPaymentMethod());
        ride.setLastUpdateAt(LocalDateTime.now());
    }

    private static void fillInRideOnPatch(Ride ride, RidePatchDTO ridePatchDTO) {
        PatchUtil.patchIfNotNull(ridePatchDTO.getOriginLatitude(), ride::setOriginLatitude);
        PatchUtil.patchIfNotNull(ridePatchDTO.getOriginLongitude(), ride::setOriginLongitude);
        PatchUtil.patchIfNotNull(ridePatchDTO.getDestinationLatitude(), ride::setDestinationLatitude);
        PatchUtil.patchIfNotNull(ridePatchDTO.getDestinationLongitude(), ride::setDestinationLongitude);
        PatchUtil.patchIfNotNull(ridePatchDTO.getSeatsCount(), ride::setSeatsCount);
        PatchUtil.patchIfNotNull(ridePatchDTO.getCarCategory(), ride::setCarCategory);
        PatchUtil.patchIfNotNull(ridePatchDTO.getPaymentMethod(), ride::setPaymentMethod);
        ride.setLastUpdateAt(LocalDateTime.now());
    }
}
