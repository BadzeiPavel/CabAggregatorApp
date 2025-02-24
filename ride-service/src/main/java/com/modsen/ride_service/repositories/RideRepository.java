package com.modsen.ride_service.repositories;

import com.modsen.ride_service.exceptions.RideNotFoundException;
import com.modsen.ride_service.models.entitties.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {
    List<Ride> findByPassengerId(UUID passengerId);
    List<Ride> findByDriverId(UUID driverId);
    List<Ride> findByPassengerIdAndCreatedAtIsBetween(UUID passengerId, LocalDateTime from, LocalDateTime to);
    List<Ride> findByDriverIdAndCreatedAtIsBetween(UUID passengerId, LocalDateTime from, LocalDateTime to);

    default Ride checkRideExistence(UUID id) {
        return findById(id).orElseThrow(() -> new RideNotFoundException("Ride entity with id='%s' not found".formatted(id)));
    }
}
