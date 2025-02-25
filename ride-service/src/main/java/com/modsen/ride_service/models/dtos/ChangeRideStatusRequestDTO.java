package com.modsen.ride_service.models.dtos;

import com.modsen.ride_service.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRideStatusRequestDTO {
    private UUID driverId;
    private RideStatus rideStatus;
}
