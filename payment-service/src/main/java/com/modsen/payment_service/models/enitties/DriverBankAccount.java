package com.modsen.payment_service.models.enitties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Driver ID cannot be empty")
    @Size(min = 1, max = 50, message = "Driver ID must be between 1 and 50 characters")
    private String driverId;

    @NotNull(message = "Balance cannot be null")
    @Positive(message = "Balance cannot be negative")
    private BigDecimal balance;
}
