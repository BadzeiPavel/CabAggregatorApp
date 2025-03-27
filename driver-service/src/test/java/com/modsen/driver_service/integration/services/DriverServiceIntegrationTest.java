package com.modsen.driver_service.integration.services;

import com.modsen.driver_service.exceptions.DriverNotFoundException;
import com.modsen.driver_service.feign_clients.AuthFeignClient;
import com.modsen.driver_service.models.entities.Car;
import com.modsen.driver_service.models.entities.Driver;
import com.modsen.driver_service.repositories.CarRepository;
import com.modsen.driver_service.repositories.DriverRepository;
import com.modsen.driver_service.services.DriverService;
import constants.KafkaConstants;
import enums.CarCategory;
import enums.DriverStatus;
import models.dtos.DriverDTO;
import models.dtos.GetFreeDriverNotInListRequest;
import models.dtos.UserPatchDTO;
import models.dtos.events.ChangeDriverStatusEvent;
import models.dtos.responses.FreeDriver;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DriverServiceIntegrationTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private DriverService driverService;

    @Autowired
    private CarRepository carRepository;

    @MockitoBean
    private KafkaTemplate<String, ChangeDriverStatusEvent> kafkaTemplate;

    @MockitoBean
    @Autowired
    private AuthFeignClient authFeignClient;

    private UUID driverId;
    private UUID carId;

    @BeforeEach
    void setUp() {
        // Create and save driver
        Driver driver = Driver.builder()
                .id(UUID.fromString("14680bb6-2b0c-4bf4-8f04-5e160fc5da18"))
                .username("testUser")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+1234567890")
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(DriverStatus.FREE)
                .isDeleted(false)
                .build();
        driver = driverRepository.save(driver);
        driverId = driver.getId();

        // Create and save associated car
        if (carRepository.findByDriverId(driverId).isEmpty()) {
            // Create and save associated car
            Car car = Car.builder()
                    .driverId(driverId)
                    .number("ABC123")
                    .brand("TestBrand")
                    .model("TestModel")
                    .color("Red")
                    .seatsCount((short) 4)
                    .carCategory(CarCategory.ECONOMY)
                    .isDeleted(false)
                    .build();
            carId = carRepository.save(car).getId();
        }
    }

    @Test
    void createDriverTest() {
        DriverDTO newDriver = DriverDTO.builder()
                .id(UUID.randomUUID())
                .username("newUser")
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .phone("+1234567890")
                .birthDate(LocalDate.of(1985, 5, 15))
                .build();

        DriverDTO createdDriver = driverService.createDriver(newDriver);

        assertNotNull(createdDriver.getId());
        assertEquals(DriverStatus.FREE, createdDriver.getStatus());
        assertFalse(createdDriver.isDeleted());
        
        Driver savedDriver = driverRepository.findById(createdDriver.getId()).orElseThrow();
        assertEquals("newUser", savedDriver.getUsername());
    }

    @Test
    void getDriverTest() {
        DriverDTO retrievedDriver = driverService.getDriver(driverId);
        
        assertEquals(driverId, retrievedDriver.getId());
        assertEquals("John", retrievedDriver.getFirstName());
    }

    @Test
    void getPaginatedDriversTest() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        GetAllPaginatedResponse<DriverDTO> response = driverService.getPaginatedDrivers(pageRequest);
        
        assertEquals(10, response.getContent().size());
        assertEquals("John", response.getContent().get(0).getFirstName());
    }

    @Test
    void getFreeDriverNotInListTest() {
        GetFreeDriverNotInListRequest request = new GetFreeDriverNotInListRequest(
                List.of(UUID.randomUUID()),
                (short) 4,
                CarCategory.ECONOMY
        );

        FreeDriver freeDriver = driverService.getFreeDriverNotInList(request);

        assertNotNull(freeDriver);
    }

    @Test
    void updateDriverTest() {
        // Prepare the DTO to update the driver
        DriverDTO updateDTO = DriverDTO.builder()
                .username("updatedUser")
                .firstName("Johnny")
                .lastName("Doe")
                .email("johnny@example.com")
                .phone("+1111111111")
                .birthDate(LocalDate.of(1991, 2, 2))
                .build();

        // Create a mock UserPatchDTO with the same data that should be passed to the Feign client
        UserPatchDTO patchDTO = new UserPatchDTO(
                "updatedUser",
                "Johnny",
                "Doe",
                "johnny@example.com",
                "+1111111111",
                LocalDate.of(1991, 2, 2)
        );

        // Ensure that the patch method in the Feign client receives the correct data
        when(authFeignClient.patch(eq(driverId.toString()), eq(patchDTO)))
                .thenReturn(ResponseEntity.ok("Success"));

        // Call the method you're testing
        DriverDTO updatedDriver = driverService.updateDriver(driverId, updateDTO);

        // Validate the updated driver details
        assertEquals("Johnny", updatedDriver.getFirstName());
        assertEquals("johnny@example.com", updatedDriver.getEmail());

        // Verify the Feign client was called with the correct parameters
        verify(authFeignClient).patch(eq(driverId.toString()), eq(patchDTO));
    }

    @Test
    void patchDriverTest() {
        UserPatchDTO patchDTO = new UserPatchDTO(
                "patchedUser",
                "James",
                "Last",
                "email@mail.ru",
                "+1234567890",
                LocalDate.of(2000, 5, 5)
        );

        DriverDTO patchedDriver = driverService.patchDriver(driverId, patchDTO);

        assertEquals("James", patchedDriver.getFirstName());
        assertEquals("patchedUser", patchedDriver.getUsername());
        
        verify(authFeignClient).patch(eq(driverId.toString()), eq(patchDTO));
    }

    @Test
    void changeDriverStatusTest() {
        ChangeDriverStatusEvent event = new ChangeDriverStatusEvent(
                driverId,
                DriverStatus.BUSY,
                UUID.randomUUID()
        );

        driverService.changeDriverStatus(event);

        Driver updatedDriver = driverRepository.findById(driverId).orElseThrow();
        assertEquals(DriverStatus.BUSY, updatedDriver.getStatus());
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void assignCarIdTest() {
        driverService.assignCarId(driverId, carId);

        Driver updatedDriver = driverRepository.findById(driverId).orElseThrow();
        assertEquals(carId, updatedDriver.getCarId());
    }

    @Test
    void changeDriverStatus_NotFound_SendsRecoveryEvent() {
        UUID invalidId = UUID.randomUUID();
        ChangeDriverStatusEvent event = new ChangeDriverStatusEvent(
                invalidId,
                DriverStatus.BUSY,
                UUID.randomUUID()
        );

        driverService.changeDriverStatus(event);

        verify(kafkaTemplate).send(eq(KafkaConstants.RIDE_COMPLETED_RECOVERY_EVENT), any());
    }

    @Test
    void getFreeDriverNotInList_NotFound_ThrowsException() {
        GetFreeDriverNotInListRequest request = new GetFreeDriverNotInListRequest(
                List.of(),
                (short) 10,
                CarCategory.ECONOMY
        );

        assertThrows(DriverNotFoundException.class, () -> 
            driverService.getFreeDriverNotInList(request)
        );
    }

    @Test
    @Disabled
    void softDeleteDriverTest() {
        DriverDTO deletedDriver = driverService.softDeleteDriver(driverId);

        assertTrue(deletedDriver.isDeleted());
        verify(authFeignClient).delete(eq(driverId.toString()));
    }
}