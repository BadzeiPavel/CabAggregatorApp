package com.modsen.driver_service.mappers;

import com.modsen.driver_service.models.dtos.DriverDTO;
import com.modsen.driver_service.models.entities.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverDTOMapper {
    Driver toDriver(DriverDTO driverDTO);
}
