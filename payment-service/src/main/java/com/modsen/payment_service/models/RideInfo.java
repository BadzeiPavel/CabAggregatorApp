package com.modsen.payment_service.models;

import enums.CarCategory;
import enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideInfo {

    @NotNull(message = "Payment method cannot be null")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Car category cannot be null")
    private CarCategory carCategory;

    @Positive(message = "Distance must be a positive value")
    private double distance;
}
