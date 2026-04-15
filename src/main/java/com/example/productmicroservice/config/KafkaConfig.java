package com.example.productmicroservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic createTopic() {
        // Single-broker local Kafka: replication factor must be 1.
        return TopicBuilder.name("product-created-events-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
