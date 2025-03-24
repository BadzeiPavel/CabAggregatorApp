package com.modsen.driver_service.integration.controllers;

import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.models.dtos.CarPatchDTO;
import com.modsen.driver_service.models.entities.Car;
import com.modsen.driver_service.models.entities.Driver;
import com.modsen.driver_service.repositories.CarRepository;
import com.modsen.driver_service.repositories.DriverRepository;
import com.modsen.driver_service.services.CarService;
import enums.CarCategory;
import enums.DriverStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class CarServiceIntegrationTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private DriverRepository driverRepository;

    @InjectMocks
    @Autowired
    private CarService carService;

    private Car car;
    private CarDTO carDTO;
    private Driver driver;

    @BeforeEach
    public void setup() {
        // Create a Driver for the Car using Builder pattern
        driver = Driver.builder()
                .id(UUID.randomUUID())
                .username("testUsername")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@email.com")
                .phone("+1234567890")
                .status(DriverStatus.FREE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .isDeleted(false)
                .build();
        driverRepository.save(driver);

        // Create Car and associate with the Driver using Builder pattern
        car = Car.builder()
                .driverId(driver.getId())
                .number("12345")
                .brand("NewBrand")
                .model("NewModel")
                .color("Blue")
                .seatsCount((short) 5)
                .carCategory(CarCategory.COMFORT)
                .build();
        carDTO = CarDTO.builder()
                .id(car.getId())
                .driverId(car.getDriverId())
                .number(car.getNumber())
                .seatsCount(car.getSeatsCount())
                .color(car.getColor())
                .brand(car.getBrand())
                .model(car.getModel())
                .carCategory(car.getCarCategory())
                .build();
    }

    @Test
    public void createCarTest() {
        // Prepare the CarDTO to create a new car
        CarDTO newCarDTO = CarDTO.builder()
                .driverId(driver.getId())
                .number("67890")
                .seatsCount((short) 4)
                .color("Red")
                .brand("Ford")
                .model("Focus")
                .carCategory(CarCategory.ECONOMY)
                .build();

        // Call the service method to create the car
        CarDTO createdCar = carService.createCar(newCarDTO);

        // Assert that the created car has the same values as the CarDTO
        Assertions.assertEquals(newCarDTO.getDriverId(), createdCar.getDriverId());
        Assertions.assertEquals(newCarDTO.getNumber(), createdCar.getNumber());
        Assertions.assertEquals(newCarDTO.getColor(), createdCar.getColor());
        Assertions.assertEquals(newCarDTO.getBrand(), createdCar.getBrand());
        Assertions.assertEquals(newCarDTO.getModel(), createdCar.getModel());
        Assertions.assertEquals(newCarDTO.getCarCategory(), createdCar.getCarCategory());
    }

    @Test
    public void getCarByIdTest() {
        // Create the car in the repository (to simulate data in the database)
        carRepository.save(car);

        // Call the service method to get the car by ID
        CarDTO fetchedCar = carService.getCarById(car.getId());

        // Assert that the car fetched has the same ID as the original car
        Assertions.assertNotNull(fetchedCar);
        Assertions.assertEquals(car.getId(), fetchedCar.getId());
    }

    @Test
    public void updateCarTest() {
        // Create the car in the repository (to simulate data in the database)
        carRepository.save(car);

        // Prepare the update DTO
        carDTO.setColor("Black");
        carDTO.setBrand("Honda");

        // Call the service method to update the car
        CarDTO updatedCar = carService.updateCar(car.getId(), carDTO);

        // Assert that the car's color and brand are updated
        Assertions.assertEquals("Black", updatedCar.getColor());
        Assertions.assertEquals("Honda", updatedCar.getBrand());
    }

    @Test
    public void patchCarTest() {
        // Create the car in the repository (to simulate data in the database)
        carRepository.save(car);

        // Prepare the patch DTO
        CarPatchDTO patchDTO = new CarPatchDTO();
        patchDTO.setColor("Pink");

        // Call the service method to patch the car
        CarDTO patchedCar = carService.patchCar(car.getId(), patchDTO);

        // Assert that the car's color is updated
        Assertions.assertEquals("Pink", patchedCar.getColor());
    }

    @Test
    public void softDeleteCarTest() {
        // Create the car in the repository (to simulate data in the database)
        carRepository.save(car);

        // Call the service method to soft delete the car
        carService.softDeleteCar(car.getId());

        // Verify that the car is marked as deleted
        Optional<Car> deletedCar = carRepository.findById(car.getId());
        Assertions.assertTrue(deletedCar.isPresent());
        Assertions.assertTrue(deletedCar.get().isDeleted());
    }
}
