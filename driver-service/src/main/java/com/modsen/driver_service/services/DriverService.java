package com.modsen.driver_service.services;

import com.modsen.driver_service.exceptions.DriverNotFoundException;
import com.modsen.driver_service.feign_clients.AuthFeignClient;
import constants.KafkaConstants;
import enums.DriverStatus;
import com.modsen.driver_service.mappers.driver_mapper.DriverDTOMapper;
import com.modsen.driver_service.mappers.driver_mapper.DriverMapper;
import models.dtos.DriverDTO;
import com.modsen.driver_service.models.entities.Driver;
import com.modsen.driver_service.repositories.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.dtos.GetFreeDriverNotInListRequest;
import models.dtos.responses.FreeDriver;
import models.dtos.responses.GetAllPaginatedResponse;
import models.dtos.UserPatchDTO;
import models.dtos.events.ChangeDriverStatusEvent;
import models.dtos.requests.ChangeDriverStatusRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utils.PatchUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverService {

    private final KafkaTemplate<String, ChangeDriverStatusEvent> kafkaTemplate;

    private final DriverMapper driverMapper;
    private final DriverDTOMapper driverDTOMapper;
    private final AuthFeignClient authFeignClient;
    private final DriverRepository repository;

    @Transactional
    public DriverDTO createDriver(DriverDTO driverDTO) {
        log.info("Creating driver {}", driverDTO);

        Driver driver = driverDTOMapper.toDriver(driverDTO);
        fillInDriverOnCreate(driver, driverDTO);

        return driverMapper.toDriverDTO(repository.save(driver));
    }

    @Transactional(readOnly = true)
    public DriverDTO getDriver(UUID id) {
        log.info("Getting driver {}", id);

        Driver driver = repository.findDriverById(id);
        return driverMapper.toDriverDTO(driver);
    }

    @Transactional(readOnly = true)
    public GetAllPaginatedResponse<DriverDTO> getPaginatedDrivers(PageRequest pageRequest) {
        log.info("Getting paginated drivers");

        Page<Driver> driverPage = repository.findByIsDeletedFalse(pageRequest);

        return getAllPaginatedResponseDTO(driverPage);
    }

    @Transactional(readOnly = true)
    public FreeDriver getFreeDriverNotInList(GetFreeDriverNotInListRequest request) {
        FreeDriver freeDriver = repository.findFirstFreeNotInList(
                request.getDriverIdExclusions(),
                DriverStatus.FREE,
                request.getSeatsCount(),
                request.getCarCategory()
        ).orElseThrow(() ->
                new DriverNotFoundException("Driver entity with provided parameters not found. Parameters: %s"
                        .formatted(request)));

        log.info("Sending driver with id {}", freeDriver.getDriverId());

        return freeDriver;
    }

    @Transactional
    public DriverDTO updateDriver(UUID id, DriverDTO driverDTO) {
        log.info("Updating driver {}; {}", id, driverDTO);

        Driver driver = repository.findDriverById(id);
        fillInDriverOnUpdate(driver, driverDTO);

        authFeignClient.patch(id.toString(), new UserPatchDTO(
                driverDTO.getUsername(),
                driverDTO.getFirstName(),
                driverDTO.getLastName(),
                driverDTO.getEmail(),
                driverDTO.getPhone(),
                driverDTO.getBirthDate())
        );

        return driverMapper.toDriverDTO(repository.save(driver));
    }

    @Transactional
    public void changeDriverStatus(ChangeDriverStatusEvent event) {
        try {
            Driver driver = repository.findByIdAndIsDeletedFalse(event.getDriverId())
                    .orElseThrow(() -> new DriverNotFoundException("Driver with id='%s' not found"
                            .formatted(event.getDriverId())));
            driver.setStatus(event.getDriverStatus());

            repository.save(driver);

            log.info("Driver status updated {}", event);
        } catch (DriverNotFoundException e) {
            log.error("Driver with id='%s' not found: {}".formatted(event.getDriverId()), e.getMessage());
            sendRecoveryEvent(event);
        } catch (Exception e) {
            log.error("Failed to update driver status: {}", e.getMessage());
            sendRecoveryEvent(event);
        }
    }

    @Transactional
    public DriverDTO patchDriver(UUID id, UserPatchDTO userPatchDTO) {
        log.info("Patching driver {}; {}", id, userPatchDTO);

        Driver driver = repository.findDriverById(id);
        fillInDriverOnPatch(driver, userPatchDTO);

        authFeignClient.patch(id.toString(), userPatchDTO);

        return driverMapper.toDriverDTO(repository.save(driver));
    }

    @Transactional
    public void patchDriverStatus(UUID id, ChangeDriverStatusRequest requestDTO) {
        log.info("Patching driver={} status on {}", id, requestDTO);

        Driver driver = repository.findDriverById(id);
        driver.setStatus(requestDTO.getDriverStatus());

        repository.save(driver);
    }

    @Transactional
    public DriverDTO softDeleteDriver(UUID id) {
        log.info("Deleting driver {}", id);

        Driver driver = repository.findDriverById(id);
        driver.setDeleted(true);

        authFeignClient.delete(id.toString());

        return driverMapper.toDriverDTO(repository.save(driver));
    }

    public void assignCarId(UUID driverId, UUID carId) {
        Driver driver = repository.findDriverById(driverId);
        driver.setCarId(carId);
        driverMapper.toDriverDTO(repository.save(driver));
    }

    private void sendRecoveryEvent(ChangeDriverStatusEvent event) {
        kafkaTemplate.send(KafkaConstants.RIDE_COMPLETED_RECOVERY_EVENT, ChangeDriverStatusEvent.builder()
                .recoveryRideId(event.getRecoveryRideId())
                .build());
    }

    private GetAllPaginatedResponse<DriverDTO> getAllPaginatedResponseDTO(Page<Driver> driverPage) {
        List<DriverDTO> driverDTOs = driverPage.stream()
                .map(driverMapper::toDriverDTO)
                .toList();

        return new GetAllPaginatedResponse<>(
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
