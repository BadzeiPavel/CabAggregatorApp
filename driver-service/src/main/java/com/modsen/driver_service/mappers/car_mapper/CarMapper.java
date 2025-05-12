package com.modsen.driver_service.mappers.car_mapper;

import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.models.entities.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CarMapper {
    CarDTO toCarDTO(Car car);
}
