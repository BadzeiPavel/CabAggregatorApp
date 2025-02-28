package com.modsen.ride_service.models.dtos;

import com.modsen.ride_service.enums.PaymentMethod;
import com.modsen.ride_service.enums.RideStatus;
import enums.CarCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideDTO {

    private UUID id;

    @NotNull(message = "Passenger ID cannot be null")
    private UUID passengerId;

    private UUID driverId;

    @NotBlank(message = "Pickup address cannot be empty")
    @Size(max = 100, message = "Pickup address must not exceed 100 characters")
    private String pickupAddress;

    @NotBlank(message = "Destination address cannot be empty")
    @Size(max = 100, message = "Destination address must not exceed 100 characters")
    private String destinationAddress;

    @Positive(message = "Cost must be a positive value")
    private BigDecimal cost;

    private RideStatus status;

    private PaymentMethod paymentMethod;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Min(value = 1, message = "Seats must be at least 1")
    @Max(value = 5, message = "Seats must be at most 5")
    private short seatsCount;

    @NotNull(message = "Car category cannot be null")
    private CarCategory carCategory;
}
