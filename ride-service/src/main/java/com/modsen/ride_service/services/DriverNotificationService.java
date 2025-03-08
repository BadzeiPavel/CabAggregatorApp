package com.modsen.ride_service.services;

import com.modsen.ride_service.enums.NotificationStatus;
import com.modsen.ride_service.exceptions.RideNotFoundException;
import com.modsen.ride_service.mappers.ride_notification_mappers.DriverRideInfoMapper;
import com.modsen.ride_service.mappers.ride_notification_mappers.NotificationDTOMapper;
import com.modsen.ride_service.mappers.ride_notification_mappers.NotificationMapper;
import com.modsen.ride_service.models.dtos.DriverNotificationDTO;
import com.modsen.ride_service.models.entitties.DriverNotification;
import com.modsen.ride_service.repositories.DriverNotificationRepository;
import lombok.RequiredArgsConstructor;
import models.dtos.responses.GetAllPaginatedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverNotificationService {

    private final DriverNotificationRepository repository;
    private final NotificationMapper notificationMapper;
    private final NotificationDTOMapper notificationDTOMapper;
    private final DriverRideInfoMapper rideInfoMapper;

    public DriverNotificationDTO createDriverNotification(DriverNotificationDTO notificationDTO) {
        notificationDTO.setStatus(NotificationStatus.SEND);
        DriverNotification notification = notificationDTOMapper.toDriverNotification(notificationDTO);

        return notificationMapper.toDriverNotificationDTO(repository.save(notification));
    }

    @Transactional
    public GetAllPaginatedResponse<DriverNotificationDTO> getPaginatedDriverNotificationsByDriverId(
            UUID driverId,
            PageRequest pageRequest
    ) {
        Page<DriverNotification> driverNotificationPage =
                repository.findByDriverIdAndStatusIsNot(driverId, NotificationStatus.READ, pageRequest);

        List<DriverNotificationDTO> notificationDTOs = driverNotificationPage.stream()
                .map(notificationMapper::toDriverNotificationDTO)
                .toList();

        return new GetAllPaginatedResponse<>(
                notificationDTOs,
                driverNotificationPage.getTotalPages(),
                driverNotificationPage.getTotalElements()
        );
    }

    @Transactional
    public void changeStatusOnReadByRideIdAndDriverId(UUID rideId, UUID driverId) {
        DriverNotification driverNotification = repository.findByRideIdAndDriverId(rideId, driverId)
                .orElseThrow(() ->
                        new RideNotFoundException("Ride notification with driver_id='%s' and ride_id='%s' not found"
                                .formatted(driverId, rideId)));

        driverNotification.setStatus(NotificationStatus.READ);
    }
}
