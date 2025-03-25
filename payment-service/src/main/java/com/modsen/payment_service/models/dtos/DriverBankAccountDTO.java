package com.modsen.payment_service.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverBankAccountDTO {
    private String id;

    @NotBlank(message = "Driver ID cannot be empty")
    @Size(min = 1, max = 50, message = "Driver ID must be between 1 and 50 characters")
    private String driverId;

    @Positive(message = "Balance cannot be negative")
    private BigDecimal balance;
}
