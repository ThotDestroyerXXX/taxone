package com.example.taxone.listener;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.repository.NotificationRepository;
import com.example.taxone.repository.UserRepository;
import com.example.taxone.service.NotificationStrategyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationStrategyService handlerRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "notification-events", groupId = "notification-service")
    public void handleNotificationEvent(String message) {
        try {
            // Jackson automatically deserializes to correct subclass!
            NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);

            log.info("Processing {} for user: {}", event.getClass().getSimpleName(), event.getUserId());

            User user = userRepository.findById(event.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + event.getUserId()));

            // Use registry to create notification
            Notification notification = handlerRegistry.createNotification(event, user);

            // Save notification
            notificationRepository.save(notification);

            log.info("Notification created: {}", notification.getId());

        } catch (Exception e) {
            log.error("Error processing notification event", e);
        }
    }
}