package com.modsen.ride_service.mappers.ride_mappers;

import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.entitties.Ride;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RideDTOMapper {
    Ride toRide(RideDTO ratingDTO);
}
