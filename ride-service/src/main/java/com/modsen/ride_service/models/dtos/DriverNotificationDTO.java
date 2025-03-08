package com.modsen.ride_service.models.dtos;

import com.modsen.ride_service.enums.NotificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DriverNotificationDTO {

    private UUID id;

    private DriverRideInfoDTO rideInfo;

    @NotNull(message = "Ride ID cannot be null")
    private UUID rideId;

    @NotNull(message = "Driver ID cannot be null")
    private UUID driverId;

    @Enumerated(EnumType.ORDINAL)
    private NotificationStatus status;
}
