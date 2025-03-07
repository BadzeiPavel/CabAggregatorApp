package com.modsen.ride_service.listeners;

import com.modsen.ride_service.services.RideService;
import constants.KafkaConstants;
import lombok.RequiredArgsConstructor;
import models.dtos.events.ChangeDriverStatusEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideListener {

    private final RideService service;

    @KafkaListener(
            topics = KafkaConstants.RIDE_COMPLETED_RECOVERY_EVENT,
            groupId = KafkaConstants.DRIVER_SERVICE_GROUP_ID
    )
    public void handleRideCompletedEvent(ChangeDriverStatusEvent event) {
        service.recoverRide(event);

        System.out.println("Recovering ride (id='%s')".formatted(event.getRecoveryRideId()));
    }
}