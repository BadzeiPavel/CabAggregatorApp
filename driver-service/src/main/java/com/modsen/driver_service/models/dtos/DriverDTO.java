package com.modsen.driver_service.models.dtos;

import com.modsen.driver_service.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {
    private UUID id;
    private UUID carId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private DriverStatus status;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime lastModificationAt;
    private boolean isDeleted;
}
