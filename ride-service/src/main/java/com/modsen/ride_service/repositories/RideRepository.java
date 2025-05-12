package com.modsen.ride_service.repositories;

import com.modsen.ride_service.exceptions.RideNotFoundException;
import com.modsen.ride_service.models.entitties.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {
    Page<Ride> findByPassengerId(UUID passengerId, Pageable pageable);
    Page<Ride> findByPassengerIdAndCreatedAtIsBetween(
            UUID passengerId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
    Page<Ride> findByDriverId(UUID driverId, Pageable pageable);
    Page<Ride> findByDriverIdAndCreatedAtIsBetween(
            UUID driverId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    default Ride findByRideId(UUID id) {
        return findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride entity with id='%s' not found".formatted(id)));
    }
}
