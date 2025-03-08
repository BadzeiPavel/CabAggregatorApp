package com.modsen.ride_service.models.dtos;

import com.modsen.ride_service.enums.PaymentMethod;
import enums.CarCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RidePatchDTO {

    @Size(max = 100, message = "Origin address must not exceed 100 characters")
    private String originAddress;

    @Size(max = 100, message = "Destination address must not exceed 100 characters")
    private String destinationAddress;

    @Min(value = 1, message = "Seats must be at least 1")
    @Max(value = 5, message = "Seats must be at most 5")
    private Short seatsCount;

    private PaymentMethod paymentMethod;

    private CarCategory carCategory;
}
