package com.modsen.payment_service.models.dtos;

import com.modsen.payment_service.enums.PaymentStatus;
import com.modsen.payment_service.models.RideInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String id;
    private String rideId;
    private String driverId;
    private String passengerId;
    private BigDecimal cost;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String promoCode;
    private RideInfo rideInfo;
}
