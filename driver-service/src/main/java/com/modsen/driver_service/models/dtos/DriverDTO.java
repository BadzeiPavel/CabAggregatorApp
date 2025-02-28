package com.modsen.driver_service.models.dtos;

import com.modsen.driver_service.enums.DriverStatus;
import jakarta.validation.constraints.*;
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

    @Size(min = 5, message = "Username must be at least 5 characters long")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Size(min = 2, message = "First name must be at least 2 characters long")
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @Size(min = 2, message = "Last name must be at least 2 characters long")
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format"
    )
    private String email;

    @NotBlank(message = "Phone cannot be empty")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format"
    )
    private String phone;

    private DriverStatus status;

    @NotNull(message = "Birth date cannot be empty")
    private LocalDate birthDate;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdateAt;

    private boolean isDeleted;
}
