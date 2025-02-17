package com.modsen.auth_service.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDTO {

    @Size(min = 5, message = "Lastname must be at least 5 characters long")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Size(min = 5, message = "Lastname must be at least 5 characters long")
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @Size(min = 5, message = "Lastname must be at least 5 characters long")
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @NotBlank(message = "Role cannot be empty")
    private String role;
}
