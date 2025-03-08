package models.dtos.requests;

import enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDriverStatusRequest {
    private DriverStatus driverStatus;
}
