package com.modsen.driver_service.unit.services;

import com.modsen.driver_service.mappers.car_mapper.CarMapper;
import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.models.dtos.CarPatchDTO;
import com.modsen.driver_service.models.entities.Car;
import com.modsen.driver_service.repositories.CarRepository;
import com.modsen.driver_service.services.CarService;
import enums.CarCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository repository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarService carService;

    private final UUID carId = UUID.randomUUID();
    private final UUID driverId = UUID.randomUUID();
    private Car testCar;
    private CarDTO testCarDTO;

    @BeforeEach
    void setUp() {
        testCar = new Car(
                carId,
                null,
                driverId,
                "ABC123",
                (short) 4,
                "Red",
                "Toyota",
                "Camry",
                CarCategory.ECONOMY,
                null,
                null,
                false
        );

        testCarDTO = new CarDTO(
                carId,
                driverId,
                "ABC123",
                (short) 4,
                "Red",
                "Toyota",
                "Camry",
                CarCategory.ECONOMY,
                null,
                null,
                false
        );
    }

    @Test
    void updateCar_ValidIdAndDTO_UpdatesAllFields() {
        CarDTO updateDTO = new CarDTO(
                carId,
                driverId,
                "XYZ789",
                (short) 5,
                "Blue",
                "Honda",
                "Accord",
                CarCategory.BUSINESS,
                null,
                null,
                false
        );

        when(repository.getCarById(carId)).thenReturn(testCar);
        when(repository.save(testCar)).thenReturn(testCar);
        when(carMapper.toCarDTO(testCar)).thenReturn(updateDTO);

        CarDTO result = carService.updateCar(carId, updateDTO);

        assertEquals(updateDTO.getNumber(), testCar.getNumber());
        assertEquals(updateDTO.getSeatsCount(), testCar.getSeatsCount());
        assertEquals(updateDTO.getColor(), testCar.getColor());
        assertEquals(updateDTO.getBrand(), testCar.getBrand());
        assertEquals(updateDTO.getModel(), testCar.getModel());
        assertEquals(updateDTO.getCarCategory(), testCar.getCarCategory());
        assertEquals(updateDTO, result);
    }

    @Test
    void patchCar_PartialUpdate_UpdatesNonNullFields() {
        CarPatchDTO patchDTO = new CarPatchDTO();
        patchDTO.setNumber("UPD123");
        patchDTO.setColor("Green");
        patchDTO.setModel("Corolla");

        when(repository.getCarById(carId)).thenReturn(testCar);
        when(repository.save(testCar)).thenReturn(testCar);
        when(carMapper.toCarDTO(testCar)).thenReturn(testCarDTO);

        carService.patchCar(carId, patchDTO);

        assertEquals(patchDTO.getNumber(), testCar.getNumber());
        assertEquals(patchDTO.getColor(), testCar.getColor());
        assertEquals(patchDTO.getModel(), testCar.getModel());
        assertNotNull(testCar.getLastUpdateAt());

        assertEquals(4, testCar.getSeatsCount());
        assertEquals("Toyota", testCar.getBrand());
    }

    @Test
    void softDeleteCar_ValidId_MarksAsDeleted() {
        when(repository.getCarById(carId)).thenReturn(testCar);
        when(repository.save(testCar)).thenReturn(testCar);
        when(carMapper.toCarDTO(testCar)).thenReturn(testCarDTO);

        CarDTO result = carService.softDeleteCar(carId);

        assertTrue(testCar.isDeleted());
        assertEquals(testCarDTO, result);
    }
}
