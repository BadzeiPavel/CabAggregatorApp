package com.modsen.auth_service.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogoutDTO {

    @NotBlank(message = "Refresh token cannot be empty")
    private String refreshToken;
}
