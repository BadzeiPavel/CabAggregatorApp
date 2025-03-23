package com.modsen.ride_service.services;

import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.exceptions.InvalidRideStatusException;
import com.modsen.ride_service.exceptions.RideNotFoundException;
import com.modsen.ride_service.mappers.ride_mappers.RideDTOMapper;
import com.modsen.ride_service.mappers.ride_mappers.RideMapper;
import com.modsen.ride_service.models.dtos.ChangeRideStatusRequestDTO;
import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.dtos.RidePatchDTO;
import com.modsen.ride_service.models.entitties.Ride;
import com.modsen.ride_service.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllPaginatedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    private final DriverNotificationService driverNotificationService;

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

    public GetAllPaginatedResponseDTO<RideDTO> getPaginatedRidesByPassengerId(
            UUID passengerId,
            PageRequest pageRequest
    ) {
        Page<Ride> ridePage = repository.findByPassengerId(passengerId, pageRequest);

        return getAllPaginatedResponseDTO(ridePage);
    }

    public GetAllPaginatedResponseDTO<RideDTO> getPaginatedPassengerRidesInDateRange(
            UUID passengerId,
            LocalDateTime from,
            LocalDateTime to,
            PageRequest pageRequest
    ) {
        Page<Ride> ridePage = repository.findByPassengerIdAndCreatedAtIsBetween(passengerId, from, to, pageRequest);

        return getAllPaginatedResponseDTO(ridePage);
    }

    public GetAllPaginatedResponseDTO<RideDTO> getPaginatedDriverRidesInDateRange(
            UUID driverId,
            LocalDateTime from,
            LocalDateTime to,
            PageRequest pageRequest
    ) {
        Page<Ride> ridePage = repository.findByDriverIdAndCreatedAtIsBetween(driverId, from, to, pageRequest);

        return getAllPaginatedResponseDTO(ridePage);
    }

    public GetAllPaginatedResponseDTO<RideDTO> getPaginatedRidesByDriverId(UUID driverId, PageRequest pageRequest) {
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
    public RideDTO changeRideStatus(UUID rideId, ChangeRideStatusRequestDTO requestDTO) {
        RideStatus requestRideStatus = requestDTO.getRideStatus();
        Ride ride = changeRideStatus(rideId, requestRideStatus);

        return rideMapper.toRideDTO(repository.save(ride));
    }

    // TODO: send request to driver-service via kafka to set driver.status to 'BUSY'
    @Transactional
    public RideDTO approveDriverRequestByRideIdAndDriverId(UUID rideId, UUID driverId) {
        RideDTO rideDTO = changeRideStatus(rideId, new ChangeRideStatusRequestDTO(driverId, RideStatus.ACCEPTED));
        rideDTO.setDriverId(driverId);

        Ride savedRide = repository.save(rideDTOMapper.toRide(rideDTO));
        driverNotificationService.changeDriverNotificationOnReadByRideId(rideId);

        return rideMapper.toRideDTO(repository.save(savedRide));
    }

    // TODO sen request to driver-service to set driver.status='FREE'
    @Transactional
    public RideDTO rejectDriverRequestByRideId(UUID rideId) {
        RideDTO rideDTO = changeRideStatus(rideId, new ChangeRideStatusRequestDTO(null, RideStatus.REQUESTED));
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

    private Ride changeRideStatus(UUID id, RideStatus rideStatus) {
        Ride ride = repository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride entity with id='%s' cannot be found"
                        .formatted(id)));

        if(!rideStatus.canBeObtainedFrom().contains(ride.getStatus())) {
            throw new InvalidRideStatusException("Status '%s' cannot be set".formatted(rideStatus));
        }

        ride.setStatus(rideStatus);
        switch(ride.getStatus()) {
            case IN_RIDE -> ride.setStartTime(LocalDateTime.now());
            case COMPLETED -> {
                // TODO send req via Kafka to payment-service
                ride.setEndTime(LocalDateTime.now());
            }
            case REQUESTED -> ride.setDriverId(null);
        }

        return ride;
    }

    private GetAllPaginatedResponseDTO<RideDTO> getAllPaginatedResponseDTO(Page<Ride> ridePage) {
        List<RideDTO> rideDTOs = ridePage.stream()
                .map(rideMapper::toRideDTO)
                .toList();

        return new GetAllPaginatedResponseDTO<>(
                rideDTOs,
                ridePage.getTotalPages(),
                ridePage.getTotalElements()
        );
    }

    private static void fillInRideOnCreation(RideDTO rideDTO) {
        // TODO call payment-service to calculate costs
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
