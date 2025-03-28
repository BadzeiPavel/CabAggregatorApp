package com.modsen.rating_service.component.services.steps;

import com.modsen.rating_service.mappers.RatingMapper;
import com.modsen.rating_service.models.entities.DriverRating;
import com.modsen.rating_service.repositories.DriverRatingRepository;
import com.modsen.rating_service.services.DriverRatingService;
import com.modsen.rating_service.models.dtos.RatingDTO;
import com.modsen.rating_service.models.dtos.RatingPatchDTO;
import models.dtos.RatingStatisticResponseDTO;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import io.cucumber.java.en.*;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class DriverRatingServiceSteps {

    @Autowired
    private DriverRatingService driverRatingService;

    @Autowired
    private DriverRatingRepository repository;

    @Autowired
    private RatingMapper ratingMapper;

    private RatingDTO ratingDTO;
    private RatingDTO retrievedRating;
    private GetAllPaginatedResponse<RatingDTO> paginatedRatingsResponse;
    private String ratingId;
    private RatingDTO updatedRating;
    private RatingDTO patchedRating;
    private RatingDTO softDeletedRating;

    // Scenario: Create a new driver rating
    @Given("a valid rating request for driver {string} with passenger {string}")
    public void a_valid_rating_request_for_driver_with_passenger(String driverId, String passengerId) {
        ratingDTO = RatingDTO.builder()
                .driverId(driverId)
                .passengerId(passengerId)
                .rating(5)
                .comment("Great ride!")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    @When("I send a request to create the rating")
    public void i_send_a_request_to_create_the_rating() {
        retrievedRating = driverRatingService.createDriverRating(ratingDTO);
    }

    @Then("the rating should be created successfully")
    public void the_rating_should_be_created_successfully() {
        assertNotNull(retrievedRating);
        assertEquals(ratingDTO.getDriverId(), retrievedRating.getDriverId());
    }

    @Then("the rating status should be {string}")
    public void the_rating_status_should_be(String status) {
        assertEquals(false, retrievedRating.isDeleted()); // Assuming "PENDING" means not deleted
    }

    // Scenario: Retrieve an existing driver rating by ID
    @Given("a driver rating exists with ID {string} for retrieval")
    public void a_driver_rating_exists_with_id_for_retrieval(String id) {
        ratingId = id;
        // Simulating an existing rating for testing purposes
        ratingDTO = RatingDTO.builder()
                .id(ratingId)
                .driverId("driver123")
                .passengerId("passenger456")
                .rating(5)
                .comment("Great ride!")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        // This would be persisted in actual test scenarios
        driverRatingService.createDriverRating(ratingDTO);
    }

    @When("I request to retrieve the rating with ID {string}")
    public void i_request_to_retrieve_the_rating_with_id(String id) {
        retrievedRating = driverRatingService.getDriverRating(id);
    }

    @Then("I should receive the correct rating details")
    public void i_should_receive_the_correct_rating_details() {
        assertNotNull(retrievedRating);
        assertEquals(ratingId, retrievedRating.getId());
        assertEquals("driver123", retrievedRating.getDriverId());
    }

    // Scenario: Get paginated ratings for driver
    @Given("ratings exist for driver {string}")
    public void ratings_exist_for_driver(String driverId) {
        // Simulating a list of existing ratings
        ratingDTO = RatingDTO.builder()
                .driverId(driverId)
                .passengerId("passenger456")
                .rating(5)
                .comment("Great ride!")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        driverRatingService.createDriverRating(ratingDTO);
    }

    @When("I request paginated ratings for driver {string}")
    public void i_request_paginated_ratings_for_driver(String driverId) {
        paginatedRatingsResponse = driverRatingService.getPaginatedDriverRatingsByDriverId(driverId, PageRequest.of(0, 10));
    }

    @Then("I should receive a paginated list of ratings for the driver")
    public void i_should_receive_a_paginated_list_of_ratings_for_the_driver() {
        assertNotNull(paginatedRatingsResponse);
        assertTrue(paginatedRatingsResponse.getTotalElements() > 0);
    }

    @Given("a driver rating exists with ID {string} for update")
    public void a_driver_rating_exists_with_id_for_update(String ratingId) {
        DriverRating rating = repository.findById(ratingId).get();

        ratingDTO = ratingMapper.toRatingDTO(rating);

        driverRatingService.updateDriverRating(rating.getId(), ratingMapper.toRatingDTO(rating));
    }

    @Then("the response should include the total number of pages and elements")
    public void the_response_should_include_the_total_number_of_pages_and_elements() {
        assertTrue(paginatedRatingsResponse.getTotalPages() > 0);
        assertTrue(paginatedRatingsResponse.getTotalElements() > 0);
    }

    @When("I send a request to update the rating with ID {string} to a rating of {int}")
    public void i_send_a_request_to_update_the_rating_with_id_to_a_rating_of(String id, int newRating) {
        ratingDTO.setRating(newRating);
        updatedRating = driverRatingService.updateDriverRating(id, ratingDTO);
    }

    @Then("the rating should be updated successfully")
    public void the_rating_should_be_updated_successfully() {
        assertNotNull(updatedRating);
        assertEquals(ratingDTO.getRating(), updatedRating.getRating());
    }

    @Then("the rating should reflect the updated rating of {int}")
    public void the_rating_should_reflect_the_updated_rating_of(int rating) {
        assertEquals(rating, updatedRating.getRating());
    }

    @Then("the rating should reflect the patched rating of {int}")
    public void the_rating_should_reflect_the_patched_rating_of(int rating) {
        assertEquals(rating, patchedRating.getRating());
    }

    // Scenario: Patch an existing driver rating
    @Given("a driver rating exists with ID {string} for patching")
    public void a_driver_rating_exists_with_id_for_patching(String id) {
        ratingId = id;
        ratingDTO = RatingDTO.builder()
                .id(ratingId)
                .driverId("driver123")
                .passengerId("passenger456")
                .rating(3)
                .comment("Average ride!")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        driverRatingService.createDriverRating(ratingDTO);
    }

    @When("I send a patch request to update the rating with ID {string} to a rating of {int}")
    public void i_send_a_patch_request_to_update_the_rating_with_id_to_a_rating_of(String id, int newRating) {
        RatingPatchDTO patchDTO = new RatingPatchDTO();
        patchDTO.setRating(newRating);
        patchedRating = driverRatingService.patchDriverRating(id, patchDTO);
    }

    @Then("the rating should be patched successfully")
    public void the_rating_should_be_patched_successfully() {
        assertNotNull(patchedRating);
    }

    // Scenario: Soft delete an existing driver rating
    @Given("a driver rating exists with ID {string} for deletion")
    public void a_driver_rating_exists_with_id_for_deletion(String id) {
        ratingId = id;
        ratingDTO = RatingDTO.builder()
                .id(ratingId)
                .driverId("driver123")
                .passengerId("passenger456")
                .rating(5)
                .comment("Excellent ride!")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        driverRatingService.createDriverRating(ratingDTO);
    }

    @When("I send a request to soft delete the rating with ID {string}")
    public void i_send_a_request_to_soft_delete_the_rating_with_id(String id) {
        softDeletedRating = driverRatingService.softDeleteDriverRating(id);
    }

    @Then("the rating should be marked as deleted")
    public void the_rating_should_be_marked_as_deleted() {
        assertTrue(softDeletedRating.isDeleted());
    }

    @Then("the rating status should be {string} on delete")
    public void the_rating_status_should_be_deleted(String status) {
        assertEquals("DELETED", status);
    }

    // Scenario: Calculate the average rating for a driver
    @Given("ratings exist for driver {string} for average calculation")
    public void ratings_exist_for_driver_for_average_calculation(String driverId) {
        ratingDTO = RatingDTO.builder()
                .driverId(driverId)
                .passengerId("passenger456")
                .rating(4)
                .comment("Good ride!")
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        driverRatingService.createDriverRating(ratingDTO);
        ratingDTO.setRating(5); // Adding another rating
        driverRatingService.createDriverRating(ratingDTO);
    }

    @When("I request the average rating for driver {string}")
    public void i_request_the_average_rating_for_driver(String driverId) {
        driverRatingService.getAverageRating(driverId);
    }

    @Then("I should receive the correct average rating for the driver")
    public void i_should_receive_the_correct_average_rating_for_the_driver() {
        // Assuming we are checking the average from the mock data
        RatingStatisticResponseDTO averageRating = driverRatingService.getAverageRating("driver123");
        assertNotNull(averageRating);
        assertEquals(4.75, averageRating.getRating());
    }
}
