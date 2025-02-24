package com.modsen.ride_service.mappers.ride_notification_mappers;

import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.entitties.DriverNotification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    DriverNotificationDTO toDriverNotificationDTO(DriverNotification driverNotification);
}
