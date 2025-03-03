package com.modsen.rating_service.services;

import com.modsen.rating_service.exceptions.RatingNotFoundException;
import com.modsen.rating_service.mappers.RatingDTOMapper;
import com.modsen.rating_service.mappers.RatingMapper;
import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingPatchDTO;
import com.modsen.rating_service.models.dtos.RatingStatisticResponseDTO;
import com.modsen.rating_service.models.entities.DriverRating;
import com.modsen.rating_service.repositories.DriverRatingRepository;
import com.modsen.rating_service.utils.CalculationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.PatchUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverRatingService {

    private final RatingMapper ratingMapper;
    private final RatingDTOMapper ratingDTOMapper;
    private final DriverRatingRepository repository;

    @Transactional
    public RatingDTO createDriverRating(RatingDTO ratingDTO) {
        DriverRating driverRating = ratingDTOMapper.toDriverRating(ratingDTO);
        fillInRatingOnCreation(driverRating);

        return ratingMapper.toRatingDTO(repository.save(driverRating));
    }

    @Transactional(readOnly = true)
    public RatingDTO getDriverRating(String id) {
        DriverRating driverRating = repository.getDriverRatingById(id);
        return ratingMapper.toRatingDTO(driverRating);
    }

    public List<RatingDTO> getDriverRatingsByDriverId(String id) {
        return getAllDriverRatingsByDriverId(id)
                .stream()
                .map(ratingMapper::toRatingDTO)
                .toList();
    }

    public RatingDTO updateDriverRating(String id, RatingDTO ratingDTO) {
        DriverRating driverRating = repository.getDriverRatingById(id);
        fillInRatingOnUpdate(driverRating, ratingDTO);

        return ratingMapper.toRatingDTO(repository.save(driverRating));
    }

    public RatingDTO patchDriverRating(String id, RatingPatchDTO ratingPatchDTO) {
        DriverRating driverRating = repository.getDriverRatingById(id);
        fillInRatingOnPatch(driverRating, ratingPatchDTO);

        return ratingMapper.toRatingDTO(repository.save(driverRating));
    }

    public RatingDTO softDeleteDriverRating(String id) {
        DriverRating driverRating = repository.getDriverRatingById(id);
        driverRating.setDeleted(true);

        return ratingMapper.toRatingDTO(repository.save(driverRating));
    }

    public RatingStatisticResponseDTO getAverageRating(String id) {
        List<RatingDTO> driverRatingDTOs = getDriverRatingsByDriverId(id);
        return CalculationUtil.calculateRatingStatistics(driverRatingDTOs);
    }

    private List<DriverRating> getAllDriverRatingsByDriverId(String id) {
        return Optional.ofNullable(repository.findByDriverIdAndIsDeletedFalse(id))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() ->
                        new RatingNotFoundException("There is no any record in 'driver_rating' table")
                );
    }

    private static void fillInRatingOnCreation(DriverRating driverRating) {
        driverRating.setCreatedAt(LocalDateTime.now());
        driverRating.setDeleted(false);
    }

    private static void fillInRatingOnUpdate(DriverRating driverRating, RatingDTO ratingDTO) {
        driverRating.setRating(ratingDTO.getRating());
        driverRating.setComment(ratingDTO.getComment());
    }

    private static void fillInRatingOnPatch(DriverRating driverRating, RatingPatchDTO ratingPatchDTO) {
        PatchUtil.patchIfNotNull(ratingPatchDTO.getRating(), driverRating::setRating);
        PatchUtil.patchIfNotNull(ratingPatchDTO.getComment(), driverRating::setComment);
    }
}
