package com.modsen.ride_service.models.entitties;

import com.modsen.ride_service.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "driver_notification")
public class DriverNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id",
            referencedColumnName = "id",
            insertable = false,
            updatable = false,
            nullable = false)
    private Ride ride;

    @Column(name = "ride_id")
    private UUID rideId;

    private UUID driverId;

    private NotificationStatus status;
}
