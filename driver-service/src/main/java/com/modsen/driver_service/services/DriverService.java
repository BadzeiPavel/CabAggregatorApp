package com.modsen.driver_service.services;

import com.modsen.driver_service.enums.DriverStatus;
import com.modsen.driver_service.exceptions.DriverNotFoundException;
import com.modsen.driver_service.mappers.driver_mapper.DriverDTOMapper;
import com.modsen.driver_service.mappers.driver_mapper.DriverMapper;
import com.modsen.driver_service.models.dtos.DriverDTO;
import com.modsen.driver_service.models.entities.Driver;
import com.modsen.driver_service.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverMapper driverMapper;
    private final DriverDTOMapper driverDTOMapper;
    private final DriverRepository repository;

    @Transactional
    public DriverDTO createDriver(DriverDTO driverDTO) {
        Driver driver = driverDTOMapper.toDriver(driverDTO);
        fillInDriverOnCreate(driver, driverDTO);

        return driverMapper.toDriverDTO(repository.save(driver));
    }

    @Transactional(readOnly = true)
    public DriverDTO getDriver(UUID id) {
        Driver driver = repository.getDriverById(id);
        return driverMapper.toDriverDTO(driver);
    }

    public List<DriverDTO> getDriversByStatus(DriverStatus status) {
        return repository.findByStatus(status).stream()
                .map(driverMapper::toDriverDTO)
                .toList();
    }

    public List<DriverDTO> getDrivers() {
        return repository.findByIsDeletedFalse()
                .orElseThrow(() -> new DriverNotFoundException("There is no any record in 'driver' table"))
                .stream()
                .map(driverMapper::toDriverDTO)
                .toList();
    }

    public DriverDTO updateDriver(UUID id, DriverDTO driverDTO) {
        Driver driver = repository.getDriverById(id);
        fillInDriverOnUpdate(driver, driverDTO);

        return driverMapper.toDriverDTO(repository.save(driver));
    }

    public DriverDTO softDeleteDriver(UUID id) {
        Driver driver = repository.getDriverById(id);
        driver.setDeleted(true);

        return driverMapper.toDriverDTO(repository.save(driver));
    }

    public void assignCarId(UUID driverId, UUID carId) {
        Driver driver = repository.getDriverById(driverId);
        driver.setCarId(carId);
        driverMapper.toDriverDTO(repository.save(driver));
    }

    private static void fillInDriverOnCreate(Driver driver, DriverDTO driverDTO) {
        driver.setId(driverDTO.getId());
        driver.setDeleted(false);
        driver.setStatus(DriverStatus.FREE);
        driver.setCreatedAt(LocalDateTime.now());
    }

    private static void fillInDriverOnUpdate(Driver driver, DriverDTO driverDTO) {
        driver.setUsername(driverDTO.getUsername());
        driver.setFirstName(driverDTO.getFirstName());
        driver.setLastName(driverDTO.getLastName());
        driver.setEmail(driverDTO.getEmail());
        driver.setPhone(driverDTO.getPhone());
        driver.setBirthDate(driverDTO.getBirthDate());
    }
}
