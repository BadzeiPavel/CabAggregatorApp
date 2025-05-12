package com.modsen.ride_service.services;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.mappers.ride_notification_mappers.NotificationDTOMapper;
import com.modsen.ride_service.mappers.ride_notification_mappers.NotificationMapper;
import com.modsen.ride_service.models.dtos.PassengerNotificationDTO;
import com.modsen.ride_service.models.entitties.PassengerNotification;
import com.modsen.ride_service.repositories.PassengerNotificationRepository;
import lombok.RequiredArgsConstructor;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PassengerNotificationService {

    private final PassengerNotificationRepository repository;
    private final NotificationMapper notificationMapper;
    private final NotificationDTOMapper notificationDTOMapper;

    public PassengerNotificationDTO createPassengerNotification(PassengerNotificationDTO notificationDTO) {
        notificationDTO.setStatus(NotificationStatus.SEND);
        notificationDTO.setCreatedAt(LocalDateTime.now());
        PassengerNotification notification = notificationDTOMapper.toPassengerNotification(notificationDTO);

        return notificationMapper.toPassengerNotificationDTO(repository.save(notification));
    }

    @Transactional
    public GetAllPaginatedResponse<PassengerNotificationDTO> getPaginatedPassengerNotificationsByPassengerId(
            UUID passengerId,
            PageRequest pageRequest
    ) {
        // Получаем страницу уведомлений
        Page<PassengerNotification> passengerNotificationPage =
                repository.findByPassengerId(passengerId, pageRequest);

        List<UUID> notificationIds = passengerNotificationPage.stream()
                .map(PassengerNotification::getId)
                .toList();

        repository.updateStatusByIds(NotificationStatus.READ, notificationIds);

        List<PassengerNotificationDTO> notificationDTOs = passengerNotificationPage.stream()
                .map(notificationMapper::toPassengerNotificationDTO)
                .toList();

        return new GetAllPaginatedResponse<>(
                notificationDTOs,
                passengerNotificationPage.getTotalPages(),
                passengerNotificationPage.getTotalElements()
        );
    }
}
