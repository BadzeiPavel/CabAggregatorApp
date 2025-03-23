package com.modsen.driver_service.models.dtos;

import enums.CarCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarPatchDTO {

    @Size(max = 20, message = "Car number must be at most 20 characters long")
    private String number;

    @Min(value = 1, message = "Seats must be at least 1")
    @Max(value = 5, message = "Seats must be at most 5")
    private Short seatsCount;

    @Size(max = 20, message = "Color must be at most 20 characters long")
    private String color;

    @Size(max = 50, message = "Brand must be at most 50 characters long")
    private String brand;

    @Size(max = 50, message = "Model must be at most 50 characters long")
    private String model;

    private CarCategory carCategory;
}
