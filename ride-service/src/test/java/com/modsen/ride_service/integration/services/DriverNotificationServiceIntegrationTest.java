package com.modsen.ride_service.integration.services;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.enums.RideStatus;
import com.modsen.ride_service.exceptions.RideNotFoundException;
import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.entitties.DriverNotification;
import com.modsen.ride_service.models.entitties.Ride;
import com.modsen.ride_service.repositories.DriverNotificationRepository;
import com.modsen.ride_service.repositories.RideRepository;
import com.modsen.ride_service.services.DriverNotificationService;
import enums.CarCategory;
import enums.PaymentMethod;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class DriverNotificationServiceIntegrationTest {

    @Autowired
    private DriverNotificationService notificationService;

    @Autowired
    private DriverNotificationRepository notificationRepository;

    @Autowired
    private RideRepository rideRepository;

    private UUID driverId;
    private UUID validRideId;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        rideRepository.deleteAll();

        Ride ride = Ride.builder()
                .passengerId(UUID.randomUUID())
                .originLatitude(40.7128)
                .originLongitude(-74.0060)
                .destinationLatitude(34.0522)
                .destinationLongitude(-118.2437)
                .originAddress("Valid Address 123")
                .destinationAddress("Valid Destination 456")
                .distance(15.5) // Add positive distance
                .cost(new BigDecimal("29.99")) // Must be positive
                .status(RideStatus.REQUESTED)
                .paymentMethod(PaymentMethod.CARD)
                .seatsCount((short) 2) // Between 1-5
                .carCategory(CarCategory.ECONOMY)
                .build();

        Ride savedRide = rideRepository.save(ride);
        validRideId = savedRide.getId();
        driverId = UUID.randomUUID();
    }

    @Test
    void createDriverNotification_ValidData_ShouldCreateNotification() {
        DriverNotificationDTO request = new DriverNotificationDTO();
        request.setRideId(validRideId);
        request.setDriverId(driverId);
        request.setPassengerRating(4.5);

        DriverNotificationDTO result = notificationService.createDriverNotification(request);

        assertThat(result.getRideId()).isEqualTo(validRideId);
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SEND);
    }

    @Test
    void getPaginatedDriverNotificationsByDriverId_ShouldReturnPaginatedResults() {
        createTestNotifications(5, driverId, validRideId);
        PageRequest pageRequest = PageRequest.of(0, 3);

        GetAllPaginatedResponse<DriverNotificationDTO> response =
                notificationService.getPaginatedDriverNotificationsByDriverId(driverId, pageRequest);

        assertThat(response.getContent()).hasSize(3);
        assertThat(response.getTotalElements()).isEqualTo(5);
    }

    @Test
    void changeStatusOnReadByRideIdAndDriverId_ValidIds_ShouldUpdateStatus() {
        DriverNotification notification = notificationRepository.save(
                DriverNotification.builder()
                        .rideId(validRideId)
                        .driverId(driverId)
                        .passengerRating(4.5)
                        .status(NotificationStatus.SEND)
                        .build()
        );

        notificationService.changeStatusOnReadByRideIdAndDriverId(validRideId, driverId);

        DriverNotification updated = notificationRepository.findById(notification.getId()).get();
        assertThat(updated.getStatus()).isEqualTo(NotificationStatus.READ);
    }

    @Test
    void changeStatusOnReadByRideIdAndDriverId_InvalidIds_ShouldThrowException() {
        UUID invalidRideId = UUID.randomUUID();
        UUID invalidDriverId = UUID.randomUUID();

        assertThatThrownBy(() ->
                notificationService.changeStatusOnReadByRideIdAndDriverId(invalidRideId, invalidDriverId)
        ).isInstanceOf(RideNotFoundException.class);
    }

    private void createTestNotifications(int count, UUID driverId, UUID rideId) {
        for (int i = 0; i < count; i++) {
            notificationRepository.save(
                    DriverNotification.builder()
                            .rideId(rideId)
                            .driverId(driverId)
                            .passengerRating(4.5)
                            .status(NotificationStatus.SEND)
                            .build()
            );
        }
    }
}