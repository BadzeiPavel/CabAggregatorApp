package com.modsen.ride_service.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.PaymentMethod;
import com.modsen.ride_service.enums.RideStatus;
import enums.CarCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RideDTO {

    private UUID id;

    @NotNull(message = "Passenger ID cannot be null")
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

    private double distance;

    private String originAddress;

    private String destinationAddress;

    private BigDecimal cost;

    private RideStatus status;

    private PaymentMethod paymentMethod;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdateAt;

    @Min(value = 1, message = "Seats must be at least 1")
    @Max(value = 5, message = "Seats must be at most 5")
    private short seatsCount;

    @NotNull(message = "Car category cannot be null")
    private CarCategory carCategory;

    private String promoCode;
}
