package com.modsen.passenger_service.mappers;

import models.dtos.PassengerDTO;
import com.modsen.passenger_service.models.entities.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PassengerMapper {
    PassengerDTO toPassengerDTO(Passenger passenger);
}
