package com.modsen.driver_service.services;

import com.modsen.driver_service.mappers.DriverDTOMapper;
import com.modsen.driver_service.mappers.DriverMapper;
import com.modsen.driver_service.models.dtos.DriverDTO;
import com.modsen.driver_service.models.entities.Driver;
import com.modsen.driver_service.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverMapper driverMapper;
    private final DriverDTOMapper driverDTOMapper;
    private final DriverRepository driverRepository;

    @Transactional
    public DriverDTO saveDriver(DriverDTO driverDTO) {
        Driver driver = driverDTOMapper.toDriver(driverDTO);
        return driverMapper.toDriverDTO(driverRepository.save(driver));
    }

    @Transactional(readOnly = true)
    public DriverDTO getDriverDTO(UUID id) {
        Driver driver = driverRepository.getDriverByUuid(id);
        return driverMapper.toDriverDTO(driver);
    }

    @Transactional
    public DriverDTO updateDriver(DriverDTO driverDTO) {
        Driver driver = driverRepository.getDriverByUuid(driverDTO.getId());
        driver.copyOf(driverDTO);

        return driverMapper.toDriverDTO(driverRepository.save(driver));
    }

    @Transactional
    public DriverDTO softDeleteDriver(UUID id) {
        Driver driver = driverRepository.getDriverByUuid(id);
        driver.setDeleted(true);

        return driverMapper.toDriverDTO(driverRepository.save(driver));
    }

}
