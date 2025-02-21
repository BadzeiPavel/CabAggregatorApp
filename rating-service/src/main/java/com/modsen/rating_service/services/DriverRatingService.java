package com.modsen.rating_service.services;

import com.modsen.rating_service.exceptions.RatingNotFoundException;
import com.modsen.rating_service.mappers.RatingDTOMapper;
import com.modsen.rating_service.mappers.RatingMapper;
import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingStatisticResponseDTO;
import com.modsen.rating_service.models.entities.DriverRating;
import com.modsen.rating_service.repositories.DriverRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverRatingService {

    private final RatingMapper ratingMapper;
    private final RatingDTOMapper ratingDTOMapper;
    private final DriverRatingRepository driverRatingRepository;

    @Transactional
    public RatingDTO saveDriverRating(RatingDTO ratingDTO) {
        DriverRating driverRating = ratingDTOMapper.toDriverRating(ratingDTO);
        return ratingMapper.toRatingDTO(driverRatingRepository.save(driverRating));
    }

    @Transactional(readOnly = true)
    public RatingDTO getDriverRatingDTO(String id) {
        DriverRating driverRating = driverRatingRepository.getDriverRatingById(id);
        return ratingMapper.toRatingDTO(driverRating);
    }

    public List<RatingDTO> getAllDriverRatingDTOsByDriverId(String id) {
        return getAllDriverRatingsByDriverId(id)
                .stream()
                .map(ratingMapper::toRatingDTO)
                .toList();
    }

    public RatingDTO updateDriverRating(String id, RatingDTO ratingDTO) {
        driverRatingRepository.checkDriverRatingExistenceById(id);
        DriverRating mappedDriverRating = ratingDTOMapper.toDriverRating(ratingDTO);

        return ratingMapper.toRatingDTO(driverRatingRepository.save(mappedDriverRating));
    }

    public RatingDTO softDeleteDriverRating(String id) {
        DriverRating driverRating = driverRatingRepository.getDriverRatingById(id);
        driverRating.setDeleted(true);

        return ratingMapper.toRatingDTO(driverRatingRepository.save(driverRating));
    }

    public RatingStatisticResponseDTO getAverageRating(String id) {
        List<RatingDTO> driverRatingDTOs = getAllDriverRatingDTOsByDriverId(id);
        return RatingStatisticResponseDTO.calculateRatingStatistics(driverRatingDTOs);
    }

    private List<DriverRating> getAllDriverRatingsByDriverId(String id) {
        return Optional.ofNullable(driverRatingRepository.findByDriverIdAndIsDeletedFalse(id))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() ->
                        new RatingNotFoundException("There is no any record in 'driver_rating' table")
                );
    }
}
