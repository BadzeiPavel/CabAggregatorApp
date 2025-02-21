package com.modsen.rating_service.services;

import com.modsen.rating_service.exceptions.RatingNotFoundException;
import com.modsen.rating_service.mappers.RatingDTOMapper;
import com.modsen.rating_service.mappers.RatingMapper;
import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingStatisticResponseDTO;
import com.modsen.rating_service.models.entities.PassengerRating;
import com.modsen.rating_service.repositories.PassengerRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PassengerRatingService {

    private final RatingMapper ratingMapper;
    private final RatingDTOMapper ratingDTOMapper;
    private final PassengerRatingRepository passengerRatingRepository;

    @Transactional
    public RatingDTO savePassengerRating(RatingDTO ratingDTO) {
        PassengerRating passengerRating = ratingDTOMapper.toPassengerRating(ratingDTO);
        return ratingMapper.toRatingDTO(passengerRatingRepository.save(passengerRating));
    }

    @Transactional(readOnly = true)
    public RatingDTO getPassengerRatingDTO(String id) {
        PassengerRating passengerRating = passengerRatingRepository.getPassengerRatingById(id);
        return ratingMapper.toRatingDTO(passengerRating);
    }

    public List<RatingDTO> getAllPassengerRatingDTOsByPassengerId(String id) {
        return getAllPassengerRatingsByPassengerId(id)
                .stream()
                .map(ratingMapper::toRatingDTO)
                .toList();
    }

    public RatingDTO updatePassengerRating(String id, RatingDTO ratingDTO) {
        passengerRatingRepository.checkPassengerRatingExistenceById(id);
        PassengerRating mappedPassengerRating = ratingDTOMapper.toPassengerRating(ratingDTO);

        return ratingMapper.toRatingDTO(passengerRatingRepository.save(mappedPassengerRating));
    }

    public RatingDTO softDeletePassengerRating(String id) {
        PassengerRating passengerRating = passengerRatingRepository.getPassengerRatingById(id);
        passengerRating.setDeleted(true);

        return ratingMapper.toRatingDTO(passengerRatingRepository.save(passengerRating));
    }

    public RatingStatisticResponseDTO getAverageRating(String id) {
        List<RatingDTO> passengerRatingDTOs = getAllPassengerRatingDTOsByPassengerId(id);
        return RatingStatisticResponseDTO.calculateRatingStatistics(passengerRatingDTOs);
    }

    private List<PassengerRating> getAllPassengerRatingsByPassengerId(String id) {
        return passengerRatingRepository.findByPassengerIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new RatingNotFoundException("There is no any record in 'passenger_rating' table")
                );
    }
}
