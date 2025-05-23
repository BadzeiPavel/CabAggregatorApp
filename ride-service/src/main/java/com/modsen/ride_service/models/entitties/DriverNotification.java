package com.modsen.ride_service.models.entitties;

import com.modsen.ride_service.enums.NotificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
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

    @Column(nullable = false)
    private UUID driverId;

    private Double passengerRating;

    @Enumerated(EnumType.ORDINAL)
    private NotificationStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
