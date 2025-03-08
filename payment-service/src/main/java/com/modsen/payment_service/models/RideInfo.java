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

    @NotBlank(message = "Origin address cannot be empty")
    @Size(min = 1, max = 100, message = "Origin address must be between 1 and 100 characters")
    private String originAddress;

    @NotBlank(message = "Destination address cannot be empty")
    @Size(min = 1, max = 100, message = "Destination address must be between 1 and 100 characters")
    private String destinationAddress;
}
