package com.neuroforge_nexus.service;

import com.neuroforge_nexus.model.KafkaEvent;
import com.neuroforge_nexus.repository.KafkaEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KafkaEventService {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventService.class);
    private final KafkaEventRepository kafkaEventRepository;

    public KafkaEventService(KafkaEventRepository kafkaEventRepository) {
        this.kafkaEventRepository = kafkaEventRepository;
    }

    public void publishTaskEvent(String taskKey, String eventType, String description) {
        String payload = String.format("{\"taskKey\":\"%s\",\"eventType\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
                taskKey, eventType, description, LocalDateTime.now());

        // Log to console (simulating Kafka Topic message production)
        log.info("Kafka Producer: Published task event to topic 'task-events' -> Type: {}, Key: {}, Message: {}",
                eventType, taskKey, description);

        // Save event in DB to render it in the UI console log
        KafkaEvent kafkaEvent = KafkaEvent.builder()
                .taskKey(taskKey)
                .eventType(eventType)
                .payload(description)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaEventRepository.save(kafkaEvent);
    }
}
