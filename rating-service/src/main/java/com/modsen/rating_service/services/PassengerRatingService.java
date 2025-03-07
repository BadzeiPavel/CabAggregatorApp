package com.modsen.rating_service.services;

import com.modsen.rating_service.exceptions.RatingNotFoundException;
import com.modsen.rating_service.mappers.RatingDTOMapper;
import com.modsen.rating_service.mappers.RatingMapper;
import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingPatchDTO;
import com.modsen.rating_service.models.dtos.RatingStatisticResponseDTO;
import com.modsen.rating_service.models.entities.PassengerRating;
import com.modsen.rating_service.repositories.PassengerRatingRepository;
import com.modsen.rating_service.utils.CalculationUtil;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllPaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.PatchUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PassengerRatingService {

    private final RatingMapper ratingMapper;
    private final RatingDTOMapper ratingDTOMapper;
    private final PassengerRatingRepository repository;

    @Transactional
    public RatingDTO createPassengerRating(RatingDTO ratingDTO) {
        PassengerRating passengerRating = ratingDTOMapper.toPassengerRating(ratingDTO);
        fillInRatingOnCreation(passengerRating);

        return ratingMapper.toRatingDTO(repository.save(passengerRating));
    }

    @Transactional(readOnly = true)
    public RatingDTO getPassengerRating(String id) {
        PassengerRating passengerRating = repository.getPassengerRatingById(id);
        return ratingMapper.toRatingDTO(passengerRating);
    }

    public GetAllPaginatedResponse<RatingDTO> getPaginatedPassengerRatingsByPassengerId(
            String id,
            PageRequest pageRequest
    ) {
        Page<PassengerRating> passengerRatingPage = repository.findByPassengerIdAndIsDeletedFalse(id, pageRequest);

        List<RatingDTO> ratingDTOs = passengerRatingPage.stream()
                .map(ratingMapper::toRatingDTO)
                .toList();

        return new GetAllPaginatedResponse<>(
                ratingDTOs,
                passengerRatingPage.getTotalPages(),
                passengerRatingPage.getTotalElements()
        );
    }

    public RatingDTO updatePassengerRating(String id, RatingDTO ratingDTO) {
        PassengerRating passengerRating = repository.getPassengerRatingById(id);
        fillInRatingOnUpdate(passengerRating, ratingDTO);

        return ratingMapper.toRatingDTO(repository.save(passengerRating));
    }

    public RatingDTO patchPassengerRating(String id, RatingPatchDTO ratingPatchDTO) {
        PassengerRating passengerRating = repository.getPassengerRatingById(id);
        fillInRatingOnPatch(passengerRating, ratingPatchDTO);

        return ratingMapper.toRatingDTO(repository.save(passengerRating));
    }

    public RatingDTO softDeletePassengerRating(String id) {
        PassengerRating passengerRating = repository.getPassengerRatingById(id);
        passengerRating.setDeleted(true);

        return ratingMapper.toRatingDTO(repository.save(passengerRating));
    }

    public RatingStatisticResponseDTO getAverageRating(String id) {
        List<RatingDTO> passengerRatingDTOs = getAllPassengerRatingsByDriverId(id);
        return CalculationUtil.calculateRatingStatistics(passengerRatingDTOs);
    }

    private List<RatingDTO> getAllPassengerRatingsByDriverId(String id) {
        List<PassengerRating> passengerRatings = Optional.ofNullable(repository.findByPassengerIdAndIsDeletedFalse(id))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() ->
                        new RatingNotFoundException("There is no any record in 'passenger_rating' table")
                );

        return passengerRatings.stream()
                .map(ratingMapper::toRatingDTO)
                .toList();
    }

    private static void fillInRatingOnCreation(PassengerRating passengerRating) {
        passengerRating.setCreatedAt(LocalDateTime.now());
        passengerRating.setDeleted(false);
    }

    private static void fillInRatingOnUpdate(PassengerRating passengerRating, RatingDTO ratingDTO) {
        passengerRating.setRating(ratingDTO.getRating());
        passengerRating.setComment(ratingDTO.getComment());
    }

    private static void fillInRatingOnPatch(PassengerRating passengerRating, RatingPatchDTO ratingPatchDTO) {
        PatchUtil.patchIfNotNull(ratingPatchDTO.getRating(), passengerRating::setRating);
        PatchUtil.patchIfNotNull(ratingPatchDTO.getComment(), passengerRating::setComment);
    }
}
