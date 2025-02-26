package com.modsen.driver_service.models.dtos;

import enums.CarCategory;
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
    private String number;
    private Byte seats;
    private String color;
    private String brand;
    private String model;
    private CarCategory carCategory;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private boolean isDeleted;
}
