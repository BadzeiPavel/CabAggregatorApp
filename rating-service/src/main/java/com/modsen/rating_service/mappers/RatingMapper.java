package com.modsen.rating_service.mappers;

import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.entities.DriverRating;
import com.modsen.rating_service.models.entities.PassengerRating;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Service;

@Service
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RatingMapper {
    RatingDTO toRatingDTO(PassengerRating passengerRating);
    RatingDTO toRatingDTO(DriverRating driverRating);
}
