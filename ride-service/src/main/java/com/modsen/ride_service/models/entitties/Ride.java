package com.modsen.ride_service.models.entitties;

import com.modsen.ride_service.enums.PaymentMethod;
import com.modsen.ride_service.enums.RideStatus;
import enums.CarCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(nullable = false)
    private UUID driverId;

    @NotBlank(message = "Pickup address cannot be empty")
    @Size(max = 100, message = "Pickup address must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String pickupAddress;

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
