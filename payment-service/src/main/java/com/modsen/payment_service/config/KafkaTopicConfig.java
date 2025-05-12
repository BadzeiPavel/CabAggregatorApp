package com.modsen.payment_service.config;

import constants.KafkaConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic ridePaymentRecoveryEventsTopic() {
        return TopicBuilder.name(KafkaConstants.RIDE_PAYMENT_ON_COMPLETE_EVENT)
                .partitions(3)
                .replicas(1)
                .build();
    }
}