package com.modsen.driver_service.listeners;

import com.modsen.driver_service.services.DriverService;
import constants.KafkaConstants;
import lombok.RequiredArgsConstructor;
import models.dtos.events.ChangeDriverStatusEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverListener {

    private final DriverService service;

    @KafkaListener(topics = KafkaConstants.RIDE_COMPLETED_EVENT, groupId = KafkaConstants.DRIVER_SERVICE_GROUP_ID)
    public void handleRideCompletedEvent(ChangeDriverStatusEvent event) {
        service.changeDriverStatus(event);
    }
}