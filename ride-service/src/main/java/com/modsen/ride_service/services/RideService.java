package com.modsen.ride_service.services;

import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.exceptions.InvalidRideStatusException;
import com.modsen.ride_service.exceptions.RideNotFoundException;
import com.modsen.ride_service.mappers.ride_mappers.RideDTOMapper;
import com.modsen.ride_service.mappers.ride_mappers.RideMapper;
import com.modsen.ride_service.models.dtos.ChangeRideStatusRequestDTO;
import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.entitties.Ride;
import com.modsen.ride_service.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<RideDTO> getRidesByPassengerId(UUID passengerId) {
        return repository.findByPassengerId(passengerId).stream()
                .map(rideMapper::toRideDTO)
                .toList();
    }

    public List<RideDTO> getPassengerRidesInDateRange(UUID passengerId,
                                                      LocalDateTime timeFrom,
                                                      LocalDateTime timeTo) {
        return repository.findByPassengerIdAndCreatedAtIsBetween(passengerId, timeFrom, timeTo).stream()
                .map(rideMapper::toRideDTO)
                .toList();
    }

    public List<RideDTO> getDriverRidesInDateRange(UUID driverId,
                                                   LocalDateTime timeFrom,
                                                   LocalDateTime timeTo) {
        return repository.findByDriverIdAndCreatedAtIsBetween(driverId, timeFrom, timeTo).stream()
                .map(rideMapper::toRideDTO)
                .toList();
    }

    public List<RideDTO> getRidesByDriverId(UUID driverId) {
        return repository.findByDriverId(driverId).stream()
                .filter(ride -> ride.getPassengerId() != null)
                .map(rideMapper::toRideDTO)
                .toList();
    }

    public RideDTO updateRide(UUID rideId, RideDTO rideDTO) {
        Ride ride = repository.checkRideExistence(rideId);
        fillInRideOnUpdate(ride, rideDTO);
        ride = rideDTOMapper.toRide(rideDTO);

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

        if (!rideStatus.canBeObtainedFrom().contains(ride.getStatus())) {
            throw new InvalidRideStatusException("Status '%s' cannot be set".formatted(rideStatus));
        }

        ride.setStatus(rideStatus);
        switch (ride.getStatus()) {
            case IN_RIDE -> ride.setStartTime(LocalDateTime.now());
            case COMPLETED -> {
                // TODO send req via Kafka to payment-service
                ride.setEndTime(LocalDateTime.now());
            }
            case REQUESTED -> ride.setDriverId(null);
        }

        return ride;
    }

    // TODO: move fillIns to utils/dto (?)
    private void fillInRideOnUpdate(Ride ride, RideDTO rideDTO) {
        rideDTO.setId(ride.getId());
        rideDTO.setUpdatedAt(LocalDateTime.now());
        rideDTO.setCost(ride.getCost());
        rideDTO.setStatus(ride.getStatus());
        rideDTO.setStartTime(ride.getStartTime());
        rideDTO.setEndTime(ride.getEndTime());
        rideDTO.setCreatedAt(ride.getCreatedAt());
        rideDTO.setUpdatedAt(ride.getUpdatedAt());
    }

    private void fillInRideOnCreation(RideDTO rideDTO) {
        // TODO call payment-service to calculate costs
        rideDTO.setCost(BigDecimal.valueOf(10));
        rideDTO.setStatus(RideStatus.REQUESTED);
        rideDTO.setStartTime(null);
        rideDTO.setEndTime(null);
        rideDTO.setCreatedAt(LocalDateTime.now());
        rideDTO.setUpdatedAt(null);
    }
}
