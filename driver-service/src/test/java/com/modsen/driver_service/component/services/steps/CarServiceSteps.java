package com.modsen.driver_service.component.services.steps;

import com.modsen.driver_service.models.dtos.CarDTO;
import com.modsen.driver_service.models.entities.Car;
import com.modsen.driver_service.models.entities.Driver;
import com.modsen.driver_service.repositories.CarRepository;
import com.modsen.driver_service.repositories.DriverRepository;
import com.modsen.driver_service.services.CarService;
import enums.CarCategory;
import enums.DriverStatus;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class CarServiceSteps {

    @Autowired
    private CarService carService;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private DriverRepository driverRepository;

    private CarDTO car;
    private UUID carId;
    private Optional<CarDTO> retrievedCar;

    private UUID driverId;

    @Before
    public void setUp() {
        // Setting up a driver for the car
        driverId = UUID.randomUUID();
        driverRepository.save(Driver.builder()
                .id(driverId)
                .username("testUser")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .phone("+1234567890")
                .birthDate(LocalDate.of(1990, 1, 1))
                .status(DriverStatus.FREE)
                .isDeleted(false)
                .build());

        // Create a car and capture carId once for use in all tests
        car = CarDTO.builder()
                .driverId(driverId)
                .number("XYZ987")
                .brand("Honda")
                .model("Civic")
                .color("Black")
                .carCategory(CarCategory.ECONOMY)
                .seatsCount((short) 4)
                .isDeleted(false)
                .build();
        car = carService.createCar(car);  // Save car and assign carId
        carId = car.getId();  // Capture the carId to be reused across tests
    }

    @Given("a car with number {string}, brand {string}, model {string}, color {string}, category {string} and {int} seats")
    public void aCarWithDetails(String number, String brand, String model, String color, String category, int seats) {
        // This step is redundant now, as car is created in @Before method
    }

    @When("the car is saved")
    public void theCarIsSaved() {
        // No need to save again, car is already saved in @Before
    }

    @Then("the car should be successfully created")
    public void theCarShouldBeSuccessfullyCreated() {
        Assertions.assertNotNull(car.getId());
    }

    @Given("a car exists with ID {string}")
    public void aCarExistsWithID(String id) {
        // The car is already created in the @Before method, no need to recreate it
        // carId is set and can be used directly
    }

    @When("the car is retrieved by ID")
    public void theCarIsRetrievedByID() {
        retrievedCar = Optional.of(carService.getCarById(carId));  // Using the carId set in @Before
    }

    @Then("the correct car details should be returned")
    public void theCorrectCarDetailsShouldBeReturned() {
        Assertions.assertTrue(retrievedCar.isPresent());
        Assertions.assertEquals(carId, retrievedCar.get().getId());
    }

    @Given("an existing car with ID {string}")
    public void anExistingCarWithID(String id) {
        // The car is already created and carId is set in @Before, no need to recreate
    }

    @When("the car's color is updated to {string}")
    public void theCarSColorIsUpdatedTo(String color) {
        // Update car color using carId already set
        car.setColor(color);
        carService.updateCar(car.getId(), car);
    }

    @Then("the car should reflect the updated color")
    public void theCarShouldReflectTheUpdatedColor() {
        retrievedCar = Optional.ofNullable(carService.getCarById(carId));  // Using the same carId
        Assertions.assertTrue(retrievedCar.isPresent());
        Assertions.assertEquals("Red", retrievedCar.get().getColor());
    }

    @When("the car is deleted")
    public void theCarIsDeleted() {
        // Use the same carId for deletion
        carService.softDeleteCar(carId);
    }

    @Then("the car should be marked as deleted")
    public void theCarShouldBeMarkedAsDeleted() {
        Optional<Car> carOptional = carRepository.findById(carId);
        Assertions.assertTrue(carOptional.isPresent());
        Assertions.assertTrue(carOptional.get().isDeleted());
    }
}
