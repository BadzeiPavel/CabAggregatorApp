package com.modsen.ride_service.models.entitties;

import com.modsen.ride_service.enums.NotificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
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

    @NotNull(message = "Ride ID cannot be null")
    @Column(name = "ride_id", nullable = false)
    private UUID rideId;

    @NotNull(message = "Driver ID cannot be null")
    @Column(nullable = false)
    private UUID driverId;

    @Enumerated(EnumType.ORDINAL)
    private NotificationStatus status;
}
