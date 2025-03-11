package com.modsen.ride_service.repositories;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.models.entitties.PassengerNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PassengerNotificationRepository extends JpaRepository<PassengerNotification, UUID> {
    Page<PassengerNotification> findByPassengerId(UUID passengerId, Pageable pageable);

    @Modifying
    @Query("UPDATE PassengerNotification n SET n.status = :status WHERE n.id IN :ids")
    void updateStatusByIds(@Param("status") NotificationStatus status, @Param("ids") List<UUID> ids);
}
