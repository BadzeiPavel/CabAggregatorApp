package com.modsen.auth_service.models.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Size(min = 5, message = "Username must be at least 5 characters long")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Size(min = 5, message = "Password must be at least 5 characters long")
    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Size(min = 2, message = "First name must be at least 2 characters long")
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @Size(min = 2, message = "Last name must be at least 2 characters long")
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone cannot be empty")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format"
    )
    private String phone;

    @NotNull(message = "Birth date cannot be empty")
    private LocalDate birthDate;

    @NotBlank(message = "Role cannot be empty")
    private String role;
}
