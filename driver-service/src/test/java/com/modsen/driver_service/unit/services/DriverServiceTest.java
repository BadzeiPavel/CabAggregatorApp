package com.modsen.driver_service.unit.services;

import com.modsen.driver_service.feign_clients.AuthFeignClient;
import com.modsen.driver_service.mappers.driver_mapper.DriverDTOMapper;
import com.modsen.driver_service.mappers.driver_mapper.DriverMapper;
import com.modsen.driver_service.models.entities.Driver;
import com.modsen.driver_service.repositories.DriverRepository;
import com.modsen.driver_service.services.DriverService;
import enums.CarCategory;
import enums.DriverStatus;
import models.dtos.DriverDTO;
import models.dtos.GetFreeDriverNotInListRequest;
import models.dtos.UserPatchDTO;
import models.dtos.events.ChangeDriverStatusEvent;
import models.dtos.responses.FreeDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository repository;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private DriverDTOMapper driverDTOMapper;

    @Mock
    private AuthFeignClient authFeignClient;

    @Mock
    private KafkaTemplate<String, ChangeDriverStatusEvent> kafkaTemplate;

    @InjectMocks private DriverService driverService;
    @Captor private ArgumentCaptor<ChangeDriverStatusEvent> eventCaptor;

    private final UUID driverId = UUID.randomUUID();
    private Driver testDriver;
    private DriverDTO testDriverDTO;

    @BeforeEach
    void setUp() {
        testDriver = new Driver(
                driverId,
                null,
                null,
                "john_doe",
                "John",
                "Doe",
                "john@example.com",
                "+123456789",
                DriverStatus.FREE,
                LocalDate.of(1990, 1, 1),
                LocalDateTime.now(),
                null,
                false
        );

        testDriverDTO = new DriverDTO(
                driverId,
                null,
                "john_doe",
                "John",
                "Doe",
                "john@example.com",
                "+123456789",
                DriverStatus.FREE,
                LocalDate.of(1990, 1, 1),
                LocalDateTime.now(),
                null,
                false
        );
    }

    @Test
    void createDriver_ValidDTO_CreatesDriver() {
        when(driverDTOMapper.toDriver(any())).thenReturn(testDriver);
        when(repository.save(any())).thenReturn(testDriver);
        when(driverMapper.toDriverDTO(any())).thenReturn(testDriverDTO);

        DriverDTO result = driverService.createDriver(testDriverDTO);

        assertNotNull(result);
        assertEquals(driverId, result.getId());
        verify(repository).save(any());
    }

    @Test
    void getFreeDriverNotInList_ValidRequest_ReturnsDriver() {
        GetFreeDriverNotInListRequest request = new GetFreeDriverNotInListRequest(
                List.of(UUID.randomUUID()),
                (short) 4,
                CarCategory.ECONOMY
        );

        FreeDriver expected = new FreeDriver(
                driverId
        );

        when(repository.findFirstFreeNotInList(
                request.getDriverIdExclusions(),
                DriverStatus.FREE,
                request.getSeatsCount(),
                request.getCarCategory()
        )).thenReturn(Optional.of(expected));

        FreeDriver result = driverService.getFreeDriverNotInList(request);

        assertEquals(expected, result);
        verify(repository).findFirstFreeNotInList(
                request.getDriverIdExclusions(),
                DriverStatus.FREE,
                request.getSeatsCount(),
                request.getCarCategory()
        );
    }

    @Test
    void changeDriverStatus_ValidEvent_UpdatesStatus() {
        UUID rideId = UUID.randomUUID();
        ChangeDriverStatusEvent event = new ChangeDriverStatusEvent(
                rideId,
                DriverStatus.BUSY,
                rideId
        );

        when(repository.findByIdAndIsDeletedFalse(any()))
                .thenReturn(Optional.of(testDriver));

        driverService.changeDriverStatus(event);

        assertEquals(DriverStatus.BUSY, testDriver.getStatus());
        verify(repository).save(testDriver);
    }

    @Test
    void updateDriver_ValidIdAndDTO_UpdatesFields() {
        DriverDTO updateDTO = new DriverDTO(
                driverId,
                null,
                "new_user",
                "Jane",
                "Smith",
                "jane@example.com",
                "+987654321",
                DriverStatus.BUSY,
                LocalDate.of(1985, 5, 15),
                null,
                null,
                false
        );

        when(repository.findDriverById(any())).thenReturn(testDriver);
        when(driverMapper.toDriverDTO(any())).thenReturn(updateDTO);

        DriverDTO result = driverService.updateDriver(driverId, updateDTO);

        assertEquals("Jane", result.getFirstName());
        verify(authFeignClient).patch(any(), any());
    }

    @Test
    void softDeleteDriver_ValidId_MarksAsDeleted() {
        when(repository.findDriverById(driverId)).thenReturn(testDriver);

        driverService.softDeleteDriver(driverId);

        assertTrue(testDriver.isDeleted());
        verify(authFeignClient).delete(driverId.toString());
        verify(repository).save(testDriver);
    }

    @Test
    void assignCarId_ValidParameters_UpdatesCarId() {
        UUID carId = UUID.randomUUID();
        when(repository.findDriverById(driverId)).thenReturn(testDriver);

        driverService.assignCarId(driverId, carId);

        assertEquals(carId, testDriver.getCarId());
        verify(repository).save(testDriver);
    }

    @Test
    void patchDriver_ValidPatch_UpdatesSelectedFields() {
        UserPatchDTO patchDTO = new UserPatchDTO();
        patchDTO.setFirstName("James");
        patchDTO.setEmail("james@example.com");

        when(repository.findDriverById(driverId)).thenReturn(testDriver);
        when(repository.save(testDriver)).thenReturn(testDriver);
        when(driverMapper.toDriverDTO(testDriver)).thenReturn(testDriverDTO);

        driverService.patchDriver(driverId, patchDTO);

        assertEquals("James", testDriver.getFirstName());
        assertEquals("james@example.com", testDriver.getEmail());
        verify(authFeignClient).patch(eq(driverId.toString()), eq(patchDTO));
    }
}
