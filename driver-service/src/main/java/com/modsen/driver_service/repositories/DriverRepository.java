package com.modsen.driver_service.repositories;

import com.modsen.driver_service.enums.DriverStatus;
import com.modsen.driver_service.exceptions.DriverNotFoundException;
import com.modsen.driver_service.models.entities.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Optional<Driver> findByIdAndIsDeletedFalse(UUID id);

    Page<Driver> findByIsDeletedFalse(Pageable pageable);
    Page<Driver> findByStatus(DriverStatus status, Pageable pageable);

    default Driver getDriverById(UUID id) {
        return findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new DriverNotFoundException("Driver entity with id='%s' not found".formatted(id)));
    }
}
