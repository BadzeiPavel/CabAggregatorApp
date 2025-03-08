package com.modsen.payment_service.models.dtos;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeBalanceRequestDTO {

    @Positive(message = "Amount cannot be negative")
    private BigDecimal amount;
}
