package com.modsen.ride_service.services;

import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.exceptions.InvalidRideStatusException;
import com.modsen.ride_service.exceptions.RideNotFoundException;
import com.modsen.ride_service.feign_clients.DriverServiceFeignClient;
import com.modsen.ride_service.mappers.ride_mappers.RideDTOMapper;
import com.modsen.ride_service.mappers.ride_mappers.RideMapper;
import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.dtos.RidePatchDTO;
import com.modsen.ride_service.models.entitties.Ride;
import com.modsen.ride_service.repositories.RideRepository;
import constants.KafkaConstants;
import enums.DriverStatus;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllPaginatedResponse;
import models.dtos.events.ChangeDriverStatusEvent;
import models.dtos.requests.ChangeDriverStatusRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.PatchUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideService {

    private List<UUID> driverIds = new ArrayList<>();

    private final DriverServiceFeignClient driverServiceFeignClient;
    private final DriverNotificationService driverNotificationService;
    private final KafkaTemplate<String, ChangeDriverStatusEvent> kafkaTemplate;

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
        Ride ride = repository.checkRideExistence(rideId);
        fillInRideOnUpdate(ride, rideDTO);

        return rideMapper.toRideDTO(repository.save(ride));
    }

    public RideDTO patchRide(UUID rideId, RidePatchDTO ridePatchDTO) {
        Ride ride = repository.checkRideExistence(rideId);
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

    public void recoverRide(ChangeDriverStatusEvent event) {
        UUID recoveryRideId = event.getRecoveryRideId();
        Ride ride = repository.findById(recoveryRideId)
                .orElseThrow(() -> new RideNotFoundException("Ride entity with id='%s' cannot be found"
                        .formatted(recoveryRideId)));
        ride.setStatus(RideStatus.IN_RIDE);
        ride.setEndTime(null);

        repository.save(ride);
    }

    public RideDTO approveDriverRequestByRideIdAndDriverId(UUID rideId, UUID driverId) {
        driverServiceFeignClient.changeDriverStatus(driverId, new ChangeDriverStatusRequestDTO(DriverStatus.BUSY));

        RideDTO rideDTO = changeRideStatus(rideId, RideStatus.ACCEPTED);
        rideDTO.setDriverId(driverId);

        Ride savedRide = repository.save(rideDTOMapper.toRide(rideDTO));
        driverNotificationService.changeDriverNotificationOnReadByRideId(rideId);

        return rideMapper.toRideDTO(savedRide);
    }

    // TODO send request to driver-service to set driver.status='FREE'
    public RideDTO rejectDriverRequestByRideId(UUID rideId) {
        RideDTO rideDTO = changeRideStatus(rideId, RideStatus.REQUESTED);
        driverNotificationService.changeDriverNotificationOnReadByRideId(rideId);

        return sendNotification(rideDTOMapper.toRide(rideDTO));
    }

    private RideDTO sendNotification(Ride ride) {
        // TODO: send synchronous request to get 'FREE' driver id and NOT IN 'driverIds' List
        //  replace line below ^
        UUID driverId = UUID.fromString("b2f1b850-4d5b-11ec-81d3-0242ac130004");
        ride.setDriverId(driverId);

        driverIds.add(driverId);

        sendDriverNotification(ride, driverId);

        return rideMapper.toRideDTO(ride);
    }

    private void sendDriverNotification(Ride ride, UUID driverId) {
        DriverNotificationDTO notificationDTO = DriverNotificationDTO.builder()
                .rideId(ride.getId())
                .driverId(driverId)
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
        switch (rideStatus) {
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
        ride.setEndTime(LocalDateTime.now());
    }

    private void handleInRideStatus(Ride ride) {
        ride.setStartTime(LocalDateTime.now());
    }

    private void handleRequestedStatus(Ride ride) {
        ride.setDriverId(null);
    }

    private static void fillInRideOnCreation(RideDTO rideDTO) {
        // TODO call payment-service to calculate costs and check if passenger balance is enough for ride
        rideDTO.setCost(BigDecimal.valueOf(10));
        rideDTO.setStatus(RideStatus.REQUESTED);
        rideDTO.setStartTime(null);
        rideDTO.setEndTime(null);
        rideDTO.setCreatedAt(LocalDateTime.now());
        rideDTO.setLastUpdateAt(null);
    }

    // TODO: move fillIns to utils/dto (?)
    private static void fillInRideOnUpdate(Ride ride, RideDTO rideDTO) {
        ride.setPassengerId(rideDTO.getPassengerId());
        ride.setPickupAddress(rideDTO.getPickupAddress());
        ride.setDestinationAddress(rideDTO.getDestinationAddress());
        ride.setSeatsCount(rideDTO.getSeatsCount());
        ride.setCarCategory(rideDTO.getCarCategory());
        ride.setPaymentMethod(rideDTO.getPaymentMethod());
        ride.setLastUpdateAt(LocalDateTime.now());
    }

    private static void fillInRideOnPatch(Ride ride, RidePatchDTO ridePatchDTO) {
        PatchUtil.patchIfNotNull(ridePatchDTO.getPickupAddress(), ride::setPickupAddress);
        PatchUtil.patchIfNotNull(ridePatchDTO.getDestinationAddress(), ride::setDestinationAddress);
        PatchUtil.patchIfNotNull(ridePatchDTO.getSeatsCount(), ride::setSeatsCount);
        PatchUtil.patchIfNotNull(ridePatchDTO.getCarCategory(), ride::setCarCategory);
        PatchUtil.patchIfNotNull(ridePatchDTO.getPaymentMethod(), ride::setPaymentMethod);
        ride.setLastUpdateAt(LocalDateTime.now());
    }
}
