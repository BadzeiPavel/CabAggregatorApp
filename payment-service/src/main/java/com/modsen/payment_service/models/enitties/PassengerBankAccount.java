package com.modsen.payment_service.models.enitties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Document(collection = "passenger_bank_account")
public class PassengerBankAccount {

    @Id
    private String id;

    @NotBlank(message = "Passenger ID cannot be empty")
    @Size(min = 1, max = 50, message = "Passenger ID must be between 1 and 50 characters")
    private String passengerId;

    @NotNull(message = "Balance cannot be null")
    @Positive(message = "Balance cannot be negative")
    private BigDecimal balance;
}
