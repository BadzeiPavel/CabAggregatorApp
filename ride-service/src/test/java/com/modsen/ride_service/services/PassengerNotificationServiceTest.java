package com.modsen.ride_service.services;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.mappers.ride_notification_mappers.NotificationDTOMapper;
import com.modsen.ride_service.mappers.ride_notification_mappers.NotificationMapper;
import com.modsen.ride_service.models.dtos.PassengerNotificationDTO;
import com.modsen.ride_service.models.entitties.PassengerNotification;
import com.modsen.ride_service.repositories.PassengerNotificationRepository;
import models.dtos.responses.GetAllPaginatedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PassengerNotificationServiceTest {

    @Mock
    private PassengerNotificationRepository repository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private NotificationDTOMapper notificationDTOMapper;

    @InjectMocks
    private PassengerNotificationService service;

    @Test
    void createPassengerNotification_Success() {
        // Given
        PassengerNotificationDTO dto = new PassengerNotificationDTO();
        dto.setPassengerId(UUID.randomUUID());
        dto.setPassengerId(UUID.randomUUID());

        PassengerNotification entity = new PassengerNotification();
        when(notificationDTOMapper.toPassengerNotification(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(notificationMapper.toPassengerNotificationDTO(entity)).thenReturn(dto);

        // When
        PassengerNotificationDTO result = service.createPassengerNotification(dto);

        // Then
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getPaginatedPassengerNotificationsByPassengerId_Success() {
        // Given
        UUID passengerId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);
        PassengerNotification notification = new PassengerNotification();
        Page<PassengerNotification> page = new PageImpl<>(List.of(notification));
        PassengerNotificationDTO dto = new PassengerNotificationDTO();

        when(repository.findByPassengerId(passengerId, pageRequest)).thenReturn(page);
        when(notificationMapper.toPassengerNotificationDTO(notification)).thenReturn(dto);

        // When
        GetAllPaginatedResponse<PassengerNotificationDTO> response =
                service.getPaginatedPassengerNotificationsByPassengerId(passengerId, pageRequest);

        // Then
        assertThat(response.getContent()).containsExactly(dto);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void createPassengerNotification_SetsCorrectTimestamps() {
        // Given
        PassengerNotificationDTO dto = new PassengerNotificationDTO();
        PassengerNotification entity = new PassengerNotification();

        when(notificationDTOMapper.toPassengerNotification(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);

        // When
        service.createPassengerNotification(dto);

        // Then
        assertThat(dto.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(dto.getStatus()).isEqualTo(NotificationStatus.SEND);
    }

    @Test
    void getPaginatedPassengerNotifications_EmptyResult() {
        // Given
        UUID passengerId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PassengerNotification> emptyPage = new PageImpl<>(List.of());

        when(repository.findByPassengerId(passengerId, pageRequest)).thenReturn(emptyPage);

        // When
        GetAllPaginatedResponse<PassengerNotificationDTO> response =
                service.getPaginatedPassengerNotificationsByPassengerId(passengerId, pageRequest);

        // Then
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
    }
}
