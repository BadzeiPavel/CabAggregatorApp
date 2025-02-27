package com.modsen.driver_service.models.dtos;

import enums.CarCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDTO {

    private UUID id;

    private UUID driverId;

    @NotBlank(message = "Car number cannot be empty")
    @Size(max = 20, message = "Car number must be at most 20 characters long")
    private String number;

    @Min(value = 1, message = "Seats must be at least 1")
    @Max(value = 5, message = "Seats must be at most 5")
    private short seatsCount;

    @NotBlank(message = "Color cannot be empty")
    @Size(max = 20, message = "Color must be at most 20 characters long")
    private String color;

    @NotBlank(message = "Brand cannot be empty")
    @Size(max = 50, message = "Brand must be at most 50 characters long")
    private String brand;

    @NotBlank(message = "Model cannot be empty")
    @Size(max = 50, message = "Model must be at most 50 characters long")
    private String model;

    @NotNull(message = "Car category cannot be empty")
    private CarCategory carCategory;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdateAt;

    private boolean isDeleted;
}
