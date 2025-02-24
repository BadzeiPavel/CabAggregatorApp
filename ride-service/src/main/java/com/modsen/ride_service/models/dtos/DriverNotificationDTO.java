package com.modsen.ride_service.models.dtos;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.models.entitties.Ride;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DriverNotificationDTO {
    private UUID id;
    private Ride ride;
    private UUID rideId;
    private UUID driverId;
    private NotificationStatus status;
}
