package models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerBankAccountDTO {

    private String id;

    @NotBlank(message = "Passenger ID cannot be empty")
    @Size(min = 1, max = 50, message = "Passenger ID must be between 1 and 50 characters")
    private String passengerId;

    @Positive(message = "Balance cannot be negative")
    private BigDecimal balance;
}
