package com.modsen.payment_service.listeners;

import com.modsen.payment_service.services.PaymentService;
import constants.KafkaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import models.dtos.events.MakePaymentOnCompleteEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentListener {

    private final PaymentService service;

    @KafkaListener(topics = KafkaConstants.RIDE_PAYMENT_ON_COMPLETE_EVENT, groupId = KafkaConstants.PAYMENT_SERVICE_GROUP_ID)
    public void handleRidePaymentOnCompleteEvent(MakePaymentOnCompleteEvent event) {
        log.info("Making payment on complete: {}", event);
        service.makePaymentOnCompletedRide(event.getRideId());
    }
}