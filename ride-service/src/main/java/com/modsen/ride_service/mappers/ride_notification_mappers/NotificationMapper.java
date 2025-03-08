package com.modsen.ride_service.mappers.ride_notification_mappers;

import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.entitties.DriverNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {DriverRideInfoMapper.class})
public interface NotificationMapper {
    @Mapping(source = "ride", target = "rideInfo")
    DriverNotificationDTO toDriverNotificationDTO(DriverNotification driverNotification);
}
