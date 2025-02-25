package com.modsen.payment_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideInfo {
    private String promoCode;
    private String pickupAddress;
    private String destinationAddress;
}
