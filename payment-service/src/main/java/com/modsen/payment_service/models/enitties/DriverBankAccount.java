package com.modsen.payment_service.models.enitties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Document(collection = "driver_bank_account")
public class DriverBankAccount {
    @Id
    private String id;
    private String driverId;
    private BigDecimal balance;
}
