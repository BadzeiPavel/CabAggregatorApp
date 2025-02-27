package com.modsen.payment_service.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideInfo {

    @NotBlank(message = "Pick-up address cannot be empty")
    @Size(min = 1, max = 100, message = "Pick-up address must be between 1 and 100 characters")
    private String pickupAddress;

    @NotBlank(message = "Destination address cannot be empty")
    @Size(min = 1, max = 100, message = "Destination address must be between 1 and 100 characters")
    private String destinationAddress;
}
