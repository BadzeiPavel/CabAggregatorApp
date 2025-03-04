package com.modsen.driver_service.services;

import com.modsen.driver_service.enums.DriverStatus;
import com.modsen.driver_service.mappers.driver_mapper.DriverDTOMapper;
import com.modsen.driver_service.mappers.driver_mapper.DriverMapper;
import com.modsen.driver_service.models.dtos.DriverDTO;
import com.modsen.driver_service.models.entities.Driver;
import com.modsen.driver_service.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import models.dtos.GetAllPaginatedResponseDTO;
import models.dtos.UserPatchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.PatchUtil;

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

    public GetAllPaginatedResponseDTO<DriverDTO> getPaginatedDrivers(PageRequest pageRequest) {
        Page<Driver> driverPage = repository.findByIsDeletedFalse(pageRequest);

        return getAllPaginatedResponseDTO(driverPage);
    }

    public GetAllPaginatedResponseDTO<DriverDTO> getPaginatedDriversByStatus(
            DriverStatus status,
            PageRequest pageRequest
    ) {
        Page<Driver> driverPage = repository.findByStatus(status, pageRequest);

        return getAllPaginatedResponseDTO(driverPage);
    }

    public DriverDTO updateDriver(UUID id, DriverDTO driverDTO) {
        Driver driver = repository.getDriverById(id);
        fillInDriverOnUpdate(driver, driverDTO);

        return driverMapper.toDriverDTO(repository.save(driver));
    }

    public DriverDTO patchDriver(UUID id, UserPatchDTO userPatchDTO) {
        Driver driver = repository.getDriverById(id);
        fillInDriverOnPatch(driver, userPatchDTO);

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

    private GetAllPaginatedResponseDTO<DriverDTO> getAllPaginatedResponseDTO(Page<Driver> driverPage) {
        List<DriverDTO> driverDTOs = driverPage.stream()
                .map(driverMapper::toDriverDTO)
                .toList();

        return new GetAllPaginatedResponseDTO<>(
                driverDTOs,
                driverPage.getTotalPages(),
                driverPage.getTotalElements()
        );
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

    private static void fillInDriverOnPatch(Driver driver, UserPatchDTO userPatchDTO) {
       PatchUtil.patchIfNotNull(userPatchDTO.getUsername(), driver::setUsername);
       PatchUtil.patchIfNotNull(userPatchDTO.getFirstName(), driver::setFirstName);
       PatchUtil.patchIfNotNull(userPatchDTO.getLastName(), driver::setLastName);
       PatchUtil.patchIfNotNull(userPatchDTO.getEmail(), driver::setEmail);
       PatchUtil.patchIfNotNull(userPatchDTO.getPhone(), driver::setPhone);
       PatchUtil.patchIfNotNull(userPatchDTO.getBirthDate(), driver::setBirthDate);
    }
}
