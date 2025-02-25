package com.modsen.payment_service.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverBankAccountDTO {
    private String id;
    private String driverId;
    private BigDecimal balance;
}
