package com.modsen.driver_service.repositories;

import com.modsen.driver_service.exceptions.DriverNotFoundException;
import com.modsen.driver_service.models.entities.Driver;
import enums.CarCategory;
import enums.DriverStatus;
import models.dtos.responses.FreeDriver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Optional<Driver> findByIdAndIsDeletedFalse(UUID id);
    Optional<Driver> findByIdAndIsDeletedFalseAndCarIdNotNull(UUID id);

    @Query("SELECT new models.dtos.responses.FreeDriver(d.id) " +
            "FROM Driver d " +
            "WHERE d.status = :status AND d.car IS NOT NULL " +
            "AND d.car.seatsCount >= :requiredSeats " +
            "AND d.car.carCategory = :requiredCarCategory " +
            "AND d.id NOT IN :exclusions " +
            "ORDER BY d.id ASC LIMIT 1")
    Optional<FreeDriver> findFirstFreeNotInList(
            @Param("exclusions") List<UUID> exclusions,
            @Param("status") DriverStatus status,
            @Param("requiredSeats") short requiredSeats,
            @Param("requiredCarCategory") CarCategory requiredCarCategory
    );

    Page<Driver> findByIsDeletedFalse(Pageable pageable);

    default Driver findDriverById(UUID id) {
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new DriverNotFoundException("Driver entity with id='%s' not found".formatted(id)));
    }

    default Driver findDriverByIdAndCarIdNotNull(UUID id) {
        return findByIdAndIsDeletedFalseAndCarIdNotNull(id)
                .orElseThrow(() ->
                        new DriverNotFoundException("Driver entity with id='%s' and car_id not null not found"
                                .formatted(id)));
    }
}
