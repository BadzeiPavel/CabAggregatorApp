package com.modsen.driver_service.repositories;

import com.modsen.driver_service.exceptions.CarNotFoundException;
import com.modsen.driver_service.models.entities.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {
    Optional<Car> findByIdAndIsDeletedFalse(UUID id);
    Optional<Car> findByDriverId(UUID driverId);

    Page<Car> findByIsDeletedFalse(Pageable pageable);

    default Car getCarById(UUID id) {
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                new CarNotFoundException("Car entity with id='%s' not found".formatted(id)));
    }
}
