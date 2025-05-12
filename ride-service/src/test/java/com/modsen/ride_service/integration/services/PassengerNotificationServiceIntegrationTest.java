package com.modsen.ride_service.integration.services;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.models.dtos.PassengerNotificationDTO;
import com.modsen.ride_service.models.entitties.PassengerNotification;
import com.modsen.ride_service.repositories.PassengerNotificationRepository;
import com.modsen.ride_service.services.PassengerNotificationService;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class PassengerNotificationServiceIntegrationTest {

    @Autowired
    private PassengerNotificationService notificationService;

    @Autowired
    private PassengerNotificationRepository repository;

    private UUID passengerId;
    private UUID rideId;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        passengerId = UUID.randomUUID();
        rideId = UUID.randomUUID();
    }

    @Test
    void createPassengerNotification_ValidData_ShouldCreateNotification() {
        PassengerNotificationDTO request = new PassengerNotificationDTO();
        request.setPassengerId(passengerId);
        request.setDriverRating(4.5);
        request.setMessage("Good");

        PassengerNotificationDTO result = notificationService.createPassengerNotification(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SEND);
        assertThat(result.getCreatedAt()).isNotNull();

        PassengerNotification persisted = repository.findById(result.getId()).get();
        assertThat(persisted.getPassengerId()).isEqualTo(passengerId);
        assertThat(persisted.getDriverRating()).isEqualTo(4.5);
    }

    @Test
    void getPaginatedPassengerNotificationsByPassengerId_ShouldReturnAndMarkAsRead() {
        // Create test data
        createTestNotifications(passengerId, 5);
        UUID otherPassengerId = UUID.randomUUID();
        createTestNotifications(otherPassengerId, 3);

        // Test pagination
        PageRequest pageRequest = PageRequest.of(0, 3);
        GetAllPaginatedResponse<PassengerNotificationDTO> response =
                notificationService.getPaginatedPassengerNotificationsByPassengerId(passengerId, pageRequest);

        // Verify response
        assertThat(response.getContent()).hasSize(3);
        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getTotalPages()).isEqualTo(2);

        // Verify status updates
        GetAllPaginatedResponse<PassengerNotificationDTO> mainNotifications = notificationService.getPaginatedPassengerNotificationsByPassengerId(passengerId, PageRequest.of(0, 5));
        assertThat(mainNotifications.getContent().stream()
                .allMatch(n -> n.getStatus() == NotificationStatus.SEND))
                .isFalse() ;

        assertThat(mainNotifications.getContent().stream()
                .allMatch(n -> n.getStatus() == NotificationStatus.SEND))
                .isFalse() ;

        // Verify other passenger's notifications remain unchanged
        GetAllPaginatedResponse<PassengerNotificationDTO> otherNotifications = notificationService.getPaginatedPassengerNotificationsByPassengerId(otherPassengerId, PageRequest.of(0, 5));
        assertThat(otherNotifications.getContent().stream()
                .allMatch(n -> n.getStatus() == NotificationStatus.READ))
                .isFalse() ;
    }

    @Test
    void getPaginatedPassengerNotificationsByPassengerId_SecondPage_ShouldUpdateRemaining() {
        createTestNotifications(passengerId, 5);

        // First page
        notificationService.getPaginatedPassengerNotificationsByPassengerId(passengerId, PageRequest.of(0, 3));

        // Second page
        GetAllPaginatedResponse<PassengerNotificationDTO> response =
                notificationService.getPaginatedPassengerNotificationsByPassengerId(passengerId, PageRequest.of(1, 3));

        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(5);

        // Verify all notifications are now READ
        assertThat(notificationService.getPaginatedPassengerNotificationsByPassengerId(passengerId, PageRequest.of(0, 5))
                .getContent().stream()
                .allMatch(n -> n.getStatus() == NotificationStatus.SEND))
                .isFalse() ;
    }

    @Test
    void getPaginatedPassengerNotificationsByPassengerId_NoNotifications_ShouldReturnEmpty() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        GetAllPaginatedResponse<PassengerNotificationDTO> response =
                notificationService.getPaginatedPassengerNotificationsByPassengerId(UUID.randomUUID(), pageRequest);

        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
    }

    private void createTestNotifications(UUID passengerId, int count) {
        for(int i = 0; i < count; i++) {
            repository.save(
                    PassengerNotification.builder()
                            .passengerId(passengerId)
                            .driverRating(4.5)
                            .message("OK")
                            .status(NotificationStatus.SEND)
                            .build()
            );
        }
    }
}