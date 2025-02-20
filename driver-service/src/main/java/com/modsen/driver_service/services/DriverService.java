package com.modsen.driver_service.services;

import com.modsen.driver_service.exceptions.DriverNotFoundException;
import com.modsen.driver_service.mappers.driver_mapper.DriverDTOMapper;
import com.modsen.driver_service.mappers.driver_mapper.DriverMapper;
import com.modsen.driver_service.models.dtos.DriverDTO;
import com.modsen.driver_service.models.entities.Driver;
import com.modsen.driver_service.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverMapper driverMapper;
    private final DriverDTOMapper driverDTOMapper;
    private final DriverRepository driverRepository;

    public DriverDTO saveDriver(DriverDTO driverDTO) {
        Driver driver = driverDTOMapper.toDriver(driverDTO);
        return driverMapper.toDriverDTO(driverRepository.save(driver));
    }

    @Transactional(readOnly = true)
    public DriverDTO getDriverDTO(UUID id) {
        Driver driver = driverRepository.getDriverById(id);
        return driverMapper.toDriverDTO(driver);
    }

    public List<DriverDTO> getAll() {
        return driverRepository.findByIsDeletedFalse()
                .orElseThrow(() -> new DriverNotFoundException("There is no any record in 'driver' table"))
                .stream()
                .map(driverMapper::toDriverDTO)
                .toList();
    }

    public DriverDTO updateDriver(DriverDTO driverDTO) {
        driverRepository.checkDriverExistenceById(driverDTO.getId());
        Driver mappedDriver = driverDTOMapper.toDriver(driverDTO);

        return driverMapper.toDriverDTO(driverRepository.save(mappedDriver));
    }

    public DriverDTO softDeleteDriver(UUID id) {
        Driver driver = driverRepository.getDriverById(id);
        driver.setDeleted(true);

        return driverMapper.toDriverDTO(driverRepository.save(driver));
    }

    public void assignCarId(UUID driverId, UUID carId) {
        Driver driver = driverRepository.getDriverById(driverId);
        driver.setCarId(carId);
        driverMapper.toDriverDTO(driverRepository.save(driver));
    }

}
