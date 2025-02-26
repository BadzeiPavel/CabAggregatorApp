package com.modsen.ride_service.services;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.mappers.ride_notification_mappers.NotificationDTOMapper;
import com.modsen.ride_service.mappers.ride_notification_mappers.NotificationMapper;
import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.entitties.DriverNotification;
import com.modsen.ride_service.repositories.DriverNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DriverNotificationService {

    private final DriverNotificationRepository repository;
    private final NotificationMapper notificationMapper;
    private final NotificationDTOMapper notificationDTOMapper;

    public DriverNotificationDTO createDriverNotification(DriverNotificationDTO notificationDTO) {
        notificationDTO.setStatus(NotificationStatus.SEND);
        DriverNotification notification = notificationDTOMapper.toDriverNotification(notificationDTO);

        return notificationMapper.toDriverNotificationDTO(repository.save(notification));
    }

    @Transactional
    public List<DriverNotificationDTO> getDriverNotificationsByDriverId(UUID driverId) {
        List<DriverNotification> driverNotifications =
                repository.findByDriverIdAndStatusIsNot(driverId, NotificationStatus.READ);
        return driverNotifications.stream()
                .map(this::changeStatusOnRead)
                .map(notificationMapper::toDriverNotificationDTO)
                .toList();
    }

    @Transactional
    public Stream<DriverNotification> changeDriverNotificationOnReadByRideId(UUID rideId) {
        return repository.findByRideIdAndStatusIsNot(rideId, NotificationStatus.READ)
                .stream()
                .map(this::changeStatusOnRead);
    }

    private DriverNotification changeStatusOnRead(DriverNotification notification) {
        notification.setStatus(NotificationStatus.READ);
        return repository.save(notification);
    }
}
