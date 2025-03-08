package com.modsen.ride_service.mappers.ride_notification_mappers;

import com.modsen.ride_service.models.dtos.DriverRideInfoDTO;
import com.modsen.ride_service.models.entitties.Ride;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverRideInfoMapper {
    DriverRideInfoDTO toDriverRideInfoDTO(Ride ride);
}
