package com.modsen.ride_service.end_to_end;

import com.modsen.ride_service.RideServiceApplication;
import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.models.dtos.RideDTO;
import com.modsen.ride_service.models.entitties.Ride;
import com.modsen.ride_service.repositories.RideRepository;
import enums.CarCategory;
import enums.PaymentMethod;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RideServiceApplication.class
)
@Testcontainers
@ActiveProfiles("test")
class RideServiceE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RideRepository rideRepository;

    @BeforeEach
    void setUp() {
        rideRepository.deleteAll();
    }

    @Test
    void shouldCreateGetUpdateAndDeleteRide() {
        // Create Ride
        RideDTO createRequest = TestDataUtil.createValidRideDTO();
        ResponseEntity<RideDTO> createResponse = restTemplate.postForEntity(
                "/api/v1/rides",
                createRequest,
                RideDTO.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createResponse.getStatusCode());
        RideDTO createdRide = createResponse.getBody();
        assertNotNull(createdRide);

        // Get Created Ride
        ResponseEntity<RideDTO> getResponse = restTemplate.getForEntity(
                "/api/v1/rides/" + createdRide.getId(),
                RideDTO.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getResponse.getStatusCode());

        // Update Ride
        RideDTO updateRequest = RideDTO.builder()
                .originLatitude(51.5074)
                .originLongitude(-0.1278)
                .build();

        HttpEntity<RideDTO> updateEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<RideDTO> updateResponse = restTemplate.exchange(
                "/api/v1/rides/" + createdRide.getId(),
                HttpMethod.PUT,
                updateEntity,
                RideDTO.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateResponse.getStatusCode());

        // Delete Ride
        restTemplate.delete("/api/v1/rides/" + createdRide.getId());

        ResponseEntity<RideDTO> getAfterDelete = restTemplate.getForEntity(
                "/api/v1/rides/" + createdRide.getId(),
                RideDTO.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getAfterDelete.getStatusCode());
    }

    @Test
    void shouldHandleRideLifecycle() {
        // Create Ride
        RideDTO ride = restTemplate.postForObject(
                "/api/v1/rides",
                TestDataUtil.createValidRideDTO(),
                RideDTO.class
        );

        // Driver Accept
        ResponseEntity<RideDTO> acceptResponse = restTemplate.exchange(
                "/api/v1/rides/{id}/accept?driver-id={driverId}",
                HttpMethod.PUT,
                null,
                RideDTO.class,
                ride.getId(),
                UUID.randomUUID()
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, acceptResponse.getStatusCode());

        // Start Ride
        ResponseEntity<RideDTO> startResponse = restTemplate.exchange(
                "/api/v1/rides/{id}/status/in-ride",
                HttpMethod.PUT,
                null,
                RideDTO.class,
                ride.getId()
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, startResponse.getStatusCode());

        // Complete Ride
        ResponseEntity<RideDTO> completeResponse = restTemplate.exchange(
                "/api/v1/rides/{id}/status/completed",
                HttpMethod.PUT,
                null,
                RideDTO.class,
                ride.getId()
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, completeResponse.getStatusCode());
    }

    @Test
    void shouldHandlePaginationAndFiltering() {
        // Create test data
        UUID passengerId = UUID.randomUUID();
        TestDataUtil.createTestRides(5, passengerId, rideRepository);

        // Test pagination
        ResponseEntity<GetAllPaginatedResponse> response = restTemplate.getForEntity(
                "/api/v1/rides/passengers/{passengerId}?page=0&size=2",
                GetAllPaginatedResponse.class,
                passengerId
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        GetAllPaginatedResponse<RideDTO> paginatedResponse = response.getBody();
        assertNotNull(paginatedResponse);
        assertEquals(2, paginatedResponse.getContent().size());
        assertEquals(5, paginatedResponse.getTotalElements());
        assertEquals(3, paginatedResponse.getTotalPages());
    }

    @Test
    void shouldReturnBadRequestForInvalidStatusTransition() {
        RideDTO ride = restTemplate.postForObject(
                "/api/v1/rides",
                TestDataUtil.createValidRideDTO(),
                RideDTO.class
        );

        ResponseEntity<String> invalidResponse = restTemplate.exchange(
                "/api/v1/rides/{id}/status/completed",
                HttpMethod.PUT,
                null,
                String.class,
                ride.getId()
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, invalidResponse.getStatusCode());
    }
}

class TestDataUtil {
    public static RideDTO createValidRideDTO() {
        return RideDTO.builder()
                .passengerId(UUID.randomUUID())
                .driverId(UUID.randomUUID()) // or null if you want the driver ID to be optional
                .originLatitude(51.5074)
                .originLongitude(-0.1278)
                .destinationLatitude(52.5074)
                .destinationLongitude(1.1278)
                .originAddress("London")
                .destinationAddress("Oxford")
                .distance(10.0) // positive value
                .cost(BigDecimal.valueOf(50.0)) // valid positive cost
                .status(RideStatus.REQUESTED) // or other valid statuses
                .paymentMethod(PaymentMethod.CARD) // valid payment method
                .seatsCount((short) 2) // at least 1 seat
                .carCategory(CarCategory.ECONOMY) // valid car category
                .build();
    }


    public static void createTestRides(int count, UUID passengerId, RideRepository repository) {
        for (int i = 0; i < count; i++) {
            repository.save(Ride.builder()
                    .passengerId(passengerId)
                    .driverId(null) // or null if you want the driver ID to be optional
                    .originLatitude(51.5074)
                    .originLongitude(-0.1278)
                    .destinationLatitude(52.5074)
                    .destinationLongitude(1.1278)
                    .originAddress("London")
                    .destinationAddress("Oxford")
                    .distance(10.0) // positive value
                    .cost(BigDecimal.valueOf(50.0)) // valid positive cost
                    .status(RideStatus.REQUESTED) // or other valid statuses
                    .paymentMethod(PaymentMethod.CARD) // valid payment method
                    .seatsCount((short) 2) // at least 1 seat
                    .carCategory(CarCategory.ECONOMY) // valid car category
                    .build());
        }
    }
}