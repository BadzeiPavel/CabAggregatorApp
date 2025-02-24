package com.modsen.ride_service.models.dtos;

import com.modsen.ride_service.enums.CarCategory;
import com.modsen.ride_service.enums.PaymentMethod;
import com.modsen.ride_service.enums.RideStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideDTO {
    private UUID id;
    private UUID passengerId;
    private UUID driverId;
    private String  pickupAddress;
    private String destinationAddress;
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private RideStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private short seatsCount;
    private CarCategory carCategory;
    private String promoCode;
}
