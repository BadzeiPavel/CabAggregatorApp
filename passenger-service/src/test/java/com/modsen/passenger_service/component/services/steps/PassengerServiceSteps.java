package com.modsen.passenger_service.component.services.steps;

import com.modsen.passenger_service.services.PassengerService;
import com.modsen.passenger_service.models.entities.Passenger;
import models.dtos.PassengerDTO;
import models.dtos.UserPatchDTO;
import org.springframework.beans.factory.annotation.Autowired;
import io.cucumber.java.en.*;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.UUID;

public class PassengerServiceSteps {

    @Autowired
    private PassengerService passengerService;

    private PassengerDTO passengerDTO;
    private PassengerDTO resultPassengerDTO;
    private UUID passengerId;
    private Passenger passenger;

    @Given("a passenger DTO is provided with all the required fields")
    public void givenAPassengerDTOIsProvided() {
        passengerDTO = new PassengerDTO();
        passengerDTO.setId(UUID.randomUUID());
        passengerDTO.setUsername("john_doe");
        passengerDTO.setFirstName("John");
        passengerDTO.setLastName("Doe");
        passengerDTO.setEmail("john.doe@example.com");
        passengerDTO.setPhone("123456789");
        passengerDTO.setBirthDate(LocalDate.of(1990, 5, 5));
    }

    @When("the passenger is created")
    public void whenThePassengerIsCreated() {
        resultPassengerDTO = passengerService.createPassenger(passengerDTO);
    }

    @Then("the passenger should be saved in the repository")
    public void thenThePassengerShouldBeSavedInTheRepository() {
        assertNotNull(resultPassengerDTO);
        assertNotNull(resultPassengerDTO.getId());
    }

    @And("the created passenger's ID should not be null")
    public void andTheCreatedPassengerIdShouldNotBeNull() {
        assertNotNull(resultPassengerDTO.getId());
    }

    @Given("a passenger exists with ID {string}")
    public void givenAPassengerExistsWithId(String id) {
        passengerId = UUID.fromString(id);
        passenger = new Passenger();
        passenger.setId(passengerId);
        passenger.setUsername("old_username");
        passenger.setFirstName("Old");
        passenger.setLastName("Name");
        passenger.setEmail("old.email@example.com");
        passenger.setPhone("987654321");
        passenger.setBirthDate(LocalDate.of(1990, 5, 5));
        passenger.setDeleted(false);
    }

    @When("the passenger is requested by ID {string}")
    public void whenThePassengerIsRequestedById(String id) {
        passengerId = UUID.fromString(id);
        resultPassengerDTO = passengerService.getPassenger(passengerId);
    }

    @Then("the passenger details are returned")
    public void thenThePassengerDetailsAreReturned() {
        assertNotNull(resultPassengerDTO);
        assertEquals(passengerId, resultPassengerDTO.getId());
    }
}
