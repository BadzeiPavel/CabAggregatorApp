package models.dtos;

import enums.CarCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFreeDriverNotInListRequest {
    private List<UUID> driverIdExclusions;
    private short seatsCount;
    private CarCategory carCategory;
}
