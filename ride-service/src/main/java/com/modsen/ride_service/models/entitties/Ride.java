package com.modsen.ride_service.models.entitties;

import enums.PaymentMethod;
import com.modsen.ride_service.enums.RideStatus;
import enums.CarCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
@Table(name = "ride")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "Passenger ID cannot be null")
    @Column(nullable = false)
    private UUID passengerId;

    private UUID driverId;

    @Min(value = -90, message = "Origin latitude must be between -90 and 90")
    @Max(value = 90, message = "Origin latitude must be between -90 and 90")
    private double originLatitude;

    @Min(value = -180, message = "Origin longitude must be between -180 and 180")
    @Max(value = 180, message = "Origin longitude must be between -180 and 180")
    private double originLongitude;

    @Min(value = -90, message = "Destination latitude must be between -90 and 90")
    @Max(value = 90, message = "Destination latitude must be between -90 and 90")
    private double destinationLatitude;

    @Min(value = -180, message = "Destination longitude must be between -180 and 180")
    @Max(value = 180, message = "Destination longitude must be between -180 and 180")
    private double destinationLongitude;

    @Positive(message = "Distance must be a positive value")
    private double distance;

    @NotBlank(message = "Origin address cannot be empty")
    @Size(max = 100, message = "Origin address must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String originAddress;

    @NotBlank(message = "Destination address cannot be empty")
    @Size(max = 100, message = "Destination address must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String destinationAddress;

    @NotNull(message = "Cost cannot be null")
    @Positive(message = "Cost must be a positive value")
    @Column(name = "cost", columnDefinition = "MONEY", nullable = false)
    private BigDecimal cost;

    @NotNull(message = "Status cannot be null")
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private RideStatus status;

    @NotNull(message = "Payment method cannot be null")
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastUpdateAt;

    @Min(value = 1, message = "Seats must be at least 1")
    @Max(value = 5, message = "Seats must be at most 5")
    @Column(nullable = false)
    private short seatsCount;

    @NotNull(message = "Car category cannot be null")
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private CarCategory carCategory;
}
