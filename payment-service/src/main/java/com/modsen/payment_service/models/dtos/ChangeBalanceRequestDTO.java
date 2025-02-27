package com.modsen.payment_service.models.dtos;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeBalanceRequestDTO {

    @Min(value = 0, message = "Amount cannot be negative")
    private BigDecimal amount;
}
