package com.modsen.ride_service.services;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.exceptions.RideNotFoundException;
import com.modsen.ride_service.mappers.ride_notification_mappers.NotificationDTOMapper;
import com.modsen.ride_service.mappers.ride_notification_mappers.NotificationMapper;
import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.entitties.DriverNotification;
import com.modsen.ride_service.repositories.DriverNotificationRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DriverNotificationServiceTest {

    @Mock
    private DriverNotificationRepository repository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private NotificationDTOMapper notificationDTOMapper;
    
    @InjectMocks
    private DriverNotificationService service;

    @Test
    void createDriverNotification_Success() {
        // Given
        DriverNotificationDTO dto = new DriverNotificationDTO();
        dto.setRideId(UUID.randomUUID());
        dto.setDriverId(UUID.randomUUID());
        
        DriverNotification entity = new DriverNotification();
        when(notificationDTOMapper.toDriverNotification(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(notificationMapper.toDriverNotificationDTO(entity)).thenReturn(dto);

        // When
        DriverNotificationDTO result = service.createDriverNotification(dto);

        // Then
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getPaginatedDriverNotificationsByDriverId_Success() {
        // Given
        UUID driverId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);
        DriverNotification notification = new DriverNotification();
        Page<DriverNotification> page = new PageImpl<>(List.of(notification));
        DriverNotificationDTO dto = new DriverNotificationDTO();
        
        when(repository.findByDriverId(driverId, pageRequest)).thenReturn(page);
        when(notificationMapper.toDriverNotificationDTO(notification)).thenReturn(dto);

        // When
        GetAllPaginatedResponse<DriverNotificationDTO> response = 
            service.getPaginatedDriverNotificationsByDriverId(driverId, pageRequest);

        // Then
        assertThat(response.getContent()).containsExactly(dto);
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    @Test
    void changeStatusOnReadByRideIdAndDriverId_Success() {
        // Given
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        DriverNotification notification = new DriverNotification();
        notification.setStatus(NotificationStatus.SEND);
        
        when(repository.findByRideIdAndDriverId(rideId, driverId))
            .thenReturn(Optional.of(notification));

        // When
        service.changeStatusOnReadByRideIdAndDriverId(rideId, driverId);

        // Then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.READ);
        verify(repository).findByRideIdAndDriverId(rideId, driverId);
    }

    @Test
    void changeStatusOnReadByRideIdAndDriverId_NotFound_ThrowsException() {
        // Given
        UUID rideId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        when(repository.findByRideIdAndDriverId(rideId, driverId))
            .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.changeStatusOnReadByRideIdAndDriverId(rideId, driverId))
            .isInstanceOf(RideNotFoundException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void createDriverNotification_SetsCorrectTimestamps() {
        // Given
        DriverNotificationDTO dto = new DriverNotificationDTO();
        DriverNotification entity = new DriverNotification();
        
        when(notificationDTOMapper.toDriverNotification(any())).thenReturn(entity);
        when(repository.save(any())).thenReturn(entity);

        // When
        service.createDriverNotification(dto);

        // Then
        assertThat(dto.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(dto.getStatus()).isEqualTo(NotificationStatus.SEND);
    }

    @Test
    void getPaginatedDriverNotifications_EmptyResult() {
        // Given
        UUID driverId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<DriverNotification> emptyPage = new PageImpl<>(List.of());
        
        when(repository.findByDriverId(driverId, pageRequest)).thenReturn(emptyPage);

        // When
        GetAllPaginatedResponse<DriverNotificationDTO> response = 
            service.getPaginatedDriverNotificationsByDriverId(driverId, pageRequest);

        // Then
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
    }
}