package com.modsen.rating_service.repositories;

import com.modsen.rating_service.exceptions.RatingNotFoundException;
import com.modsen.rating_service.models.entities.DriverRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRatingRepository extends MongoRepository<DriverRating, String> {
    List<DriverRating> findByDriverIdAndIsDeletedFalse(String id);
    Optional<DriverRating> findByIdAndIsDeletedFalse(String id);

    Page<DriverRating> findByDriverIdAndIsDeletedFalse(String id, Pageable pageable);

    default DriverRating getDriverRatingById(String id) {
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new RatingNotFoundException("Rating entity with id='%s' not found".formatted(id)));
    }
}
