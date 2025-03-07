package com.modsen.ride_service.configs;

import constants.KafkaConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic rideCompletedEventsTopic() {
        return TopicBuilder.name(KafkaConstants.RIDE_COMPLETED_EVENT)
                .partitions(3)
                .replicas(1)
                .build();
    }
}