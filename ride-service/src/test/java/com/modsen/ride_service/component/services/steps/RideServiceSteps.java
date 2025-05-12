package com.modsen.ride_service.component.services.steps;

import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.mappers.ride_mappers.RideDTOMapper;
import com.modsen.ride_service.mappers.ride_mappers.RideMapper;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.entitties.Ride;
import com.modsen.ride_service.repositories.RideRepository;
import com.modsen.ride_service.services.RideService;
import enums.CarCategory;
import enums.PaymentMethod;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
public class RideServiceSteps {

    @Autowired
    private RideService rideService;

    @Autowired
    private RideRepository repository;

    @Autowired
    private RideMapper rideMapper;

    @Autowired
    private RideDTOMapper rideDTOMapper;

    private Ride ride;
    private RideDTO rideDTO;
    private UUID rideId;
    private RideDTO createdRideDTO;

    @Before
    public void setUp() {
        // Create a ride with random details to be used for the test
        ride = Ride.builder()
                .passengerId(UUID.randomUUID())
                .driverId(null)
                .originLatitude(50.4501)
                .originLongitude(30.5001)
                .destinationLatitude(50.4502)
                .destinationLongitude(30.5002)
                .distance(100)
                .cost(BigDecimal.valueOf(20.0))
                .status(RideStatus.REQUESTED)
                .paymentMethod(PaymentMethod.CARD)
                .seatsCount((short) 4)
                .carCategory(CarCategory.ECONOMY)
                .destinationAddress("Some address")
                .originAddress("Some address")
                .build();

        rideDTO = rideMapper.toRideDTO(ride);
        ride = repository.save(ride);
        rideId = ride.getId();
        rideDTO.setId(rideId);
    }

    @Given("I have a ride with ID {string}")
    public void iHaveARideWithID(String rideId) {
        ride = rideDTOMapper.toRide(rideDTO);

        repository.save(ride); // Ensure the ride is saved in the DB
    }

    @When("I fetch the ride by ID")
    public void iFetchTheRideByID() {
        // Fetch the ride by ID from the actual service and repository
        rideDTO = rideService.getRideById(rideId);
    }

    @Then("I should receive the ride with ID {string}")
    public void iShouldReceiveTheRideWithID(String id) {
        // Verify the fetched ride's ID
        RideDTO fetchedRideDTO = rideService.getRideById(rideId);
        Assert.assertEquals(rideId, fetchedRideDTO.getId());
    }

    @Given("I have updated the ride data")
    public void iHaveUpdatedTheRideData() {
        // Update the ride data
        rideDTO.setCost(BigDecimal.valueOf(25.0));
        rideDTO.setSeatsCount((short) 3);
        rideDTO.setPaymentMethod(PaymentMethod.CARD);
    }

    @When("I update the ride")
    public void iUpdateTheRide() {
        // Call the service to update the ride
        rideDTO = rideService.updateRide(rideDTO.getId(), rideDTO);
    }

    @Then("I should receive the updated ride with the new details")
    public void iShouldReceiveTheUpdatedRideWithTheNewDetails() {
        // Verify that the ride has been updated
        RideDTO updatedRideDTO = rideService.updateRide(rideDTO.getId(), rideDTO);
        Assert.assertEquals(rideDTO.getCost(), updatedRideDTO.getCost());
        Assert.assertEquals(rideDTO.getSeatsCount(), updatedRideDTO.getSeatsCount());
    }

    @When("I change the ride status to {string}")
    public void iChangeTheRideStatusTo(String status) {
        // Change the ride status
        rideDTO.setStatus(RideStatus.valueOf(status));
        rideDTO = rideService.changeRideStatus(rideId, RideStatus.valueOf(status));
    }

    @Then("the ride status should be {string}")
    public void theRideStatusShouldBe(String status) {
        // Verify that the ride status has been updated
        RideDTO updatedRideDTO = rideService.getRideById(rideId);
        Assert.assertEquals(RideStatus.valueOf(status), updatedRideDTO.getStatus());
    }
}
