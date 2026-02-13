package com.example.taxone.service.impl;

import com.example.taxone.event.NotificationEvent;
import com.example.taxone.service.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "notification-events";

    public void publishNotificationEvent(NotificationEvent event) {
        log.info("Publishing notification event: {} for user: {}",
                event.getClass().getSimpleName(), event.getUserId());

        kafkaTemplate.send(TOPIC, event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.debug("Event published successfully: {}", event.getClass().getSimpleName());
                    } else {
                        log.error("Failed to publish event: {}", event.getClass().getSimpleName(), ex);
                    }
                });
    }
}