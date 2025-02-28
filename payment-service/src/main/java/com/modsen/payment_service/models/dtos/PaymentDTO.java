package com.modsen.payment_service.models.dtos;

import com.modsen.payment_service.enums.PaymentStatus;
import com.modsen.payment_service.models.RideInfo;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Ride ID cannot be empty")
    @Size(min = 1, max = 50, message = "Ride ID must be between 1 and 50 characters")
    private String rideId;

    @NotBlank(message = "Driver ID cannot be empty")
    @Size(min = 1, max = 50, message = "Driver ID must be between 1 and 50 characters")
    private String driverId;

    @NotBlank(message = "Passenger ID cannot be empty")
    @Size(min = 1, max = 50, message = "Passenger ID must be between 1 and 50 characters")
    private String passengerId;

    private BigDecimal cost;

    private PaymentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    @Size(max = 7, message = "Promo code cannot exceed 7 characters")
    private String promoCode;

    @NotNull(message = "RideInfo cannot be null")
    private RideInfo rideInfo;
}
