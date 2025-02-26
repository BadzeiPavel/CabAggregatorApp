package com.modsen.ride_service.models.entitties;

import com.modsen.ride_service.enums.PaymentMethod;
import com.modsen.ride_service.enums.RideStatus;
import enums.CarCategory;
import jakarta.persistence.*;
import lombok.*;

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

    private UUID passengerId;

    private UUID driverId;

    private String  pickupAddress;

    private String destinationAddress;

    @Column(name = "cost", columnDefinition = "MONEY")
    private BigDecimal cost;

    @Enumerated(EnumType.ORDINAL)
    private RideStatus status;

    @Enumerated(EnumType.ORDINAL)
    private PaymentMethod paymentMethod;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private short seatsCount;

    @Enumerated(EnumType.ORDINAL)
    private CarCategory carCategory;
}
