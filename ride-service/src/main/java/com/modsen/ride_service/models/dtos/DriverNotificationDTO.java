package com.modsen.ride_service.models.dtos;

import com.modsen.ride_service.enums.NotificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
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

    @NotNull(message = "Status cannot be null")
    private NotificationStatus status;

    private LocalDateTime createdAt;
}
