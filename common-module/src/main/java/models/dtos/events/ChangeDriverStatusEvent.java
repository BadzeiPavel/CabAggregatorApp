package models.dtos.events;

import enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDriverStatusEvent {
    private UUID driverId;
    private DriverStatus driverStatus;
    private UUID recoveryRideId;
}
