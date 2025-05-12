package com.modsen.ride_service.listeners;

import com.modsen.ride_service.services.RideService;
import constants.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.dtos.events.ChangeDriverStatusEvent;
import models.dtos.events.MakePaymentOnCompleteEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideListener {

    private final RideService service;

    @KafkaListener(
            topics = KafkaConstants.RIDE_COMPLETED_RECOVERY_EVENT,
            groupId = KafkaConstants.DRIVER_SERVICE_GROUP_ID
    )
    public void handleRideCompletedRecoveryEvent(ChangeDriverStatusEvent event) {
        log.info("Recovering ride {}", event);
        service.recoverRide(event.getRecoveryRideId());
    }

    @KafkaListener(
            topics = KafkaConstants.RIDE_PAYMENT_ON_COMPLETE_RECOVERY_EVENT,
            groupId = KafkaConstants.PAYMENT_SERVICE_GROUP_ID
    )
    public void handleRidePaymentOnCompleteRecoveryEvent(MakePaymentOnCompleteEvent event) {
        log.info("Recovering ride payment on complete {}", event);
        service.recoverRide(UUID.fromString(event.getRideId()));
    }
}