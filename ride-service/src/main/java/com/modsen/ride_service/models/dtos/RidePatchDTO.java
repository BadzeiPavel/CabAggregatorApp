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

    @Min(value = -90, message = "Origin latitude must be between -90 and 90")
    @Max(value = 90, message = "Origin latitude must be between -90 and 90")
    private double originLatitude;

    @Min(value = -180, message = "Origin longitude must be between -180 and 180")
    @Max(value = 180, message = "Origin longitude must be between -180 and 180")
    private double originLongitude;

    @Min(value = -90, message = "Destination latitude must be between -90 and 90")
    @Max(value = 90, message = "Destination latitude must be between -90 and 90")
    private double destinationLatitude;

    @Min(value = -180, message = "Destination longitude must be between -180 and 180")
    @Max(value = 180, message = "Destination longitude must be between -180 and 180")
    private double destinationLongitude;

    @Min(value = 1, message = "Seats must be at least 1")
    @Max(value = 5, message = "Seats must be at most 5")
    private Short seatsCount;

    private PaymentMethod paymentMethod;

    private CarCategory carCategory;
}
