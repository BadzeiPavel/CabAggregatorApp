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
@Document(collection = "passenger_bank_account")
public class PassengerBankAccount {
    @Id
    private String id;
    private String passengerId;
    private BigDecimal balance;
}
