package com.modsen.ride_service.models.entitties;

import com.modsen.ride_service.enums.NotificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "passenger_notification")
public class PassengerNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID passengerId;

    @NotBlank(message = "Message cannot be empty")
    @Size(max = 100, message = "Message must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String message;

    private Double driverRating;

    @Enumerated(EnumType.ORDINAL)
    private NotificationStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
