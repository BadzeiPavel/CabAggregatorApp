package com.modsen.ride_service.models.dtos;

import enums.PaymentMethod;
import enums.CarCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverRideInfoDTO {
    private String originAddress;
    private String destinationAddress;
    private BigDecimal cost;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private short seatsCount;
    private CarCategory carCategory;
}
