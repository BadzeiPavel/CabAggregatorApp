package com.modsen.driver_service.repositories;

import com.modsen.driver_service.exceptions.CarNotFoundException;
import com.modsen.driver_service.models.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {
    Optional<Car> findByIdAndIsDeletedFalse(UUID id);
    Optional<List<Car>> findByIsDeletedFalse();

    default void checkCarExistenceById(UUID id) {
        if(findByIdAndIsDeletedFalse(id).isEmpty()) {
            throw new CarNotFoundException("Car entity with id='%s' not found".formatted(id));
        }
    }

    default Car getCarByUuid(UUID id) {
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                new CarNotFoundException("Car entity with id='%s' not found".formatted(id)));
    }
}
