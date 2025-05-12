package com.modsen.ride_service.repositories;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.models.entitties.DriverNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverNotificationRepository extends JpaRepository<DriverNotification, UUID> {
    @EntityGraph(attributePaths = "ride")
    Page<DriverNotification> findByDriverId(UUID driverId, Pageable pageable);

    @EntityGraph(attributePaths = "ride")
    Optional<DriverNotification> findByRideIdAndDriverId(
            UUID rideId,
            UUID driverId
    );
}
