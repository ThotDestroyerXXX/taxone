package com.example.taxone.service.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.service.NotificationStrategyService;
import com.example.taxone.strategy.NotificationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NotificationStrategyServiceImpl implements NotificationStrategyService {

    private final List<NotificationStrategy<?>> handlers;

    public NotificationStrategyServiceImpl(List<NotificationStrategy<?>> handlers) {
        this.handlers = handlers;
        log.info("Registered {} notification handlers", handlers.size());
    }

    @SuppressWarnings("unchecked")
    public Notification createNotification(NotificationEvent event, User user) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(event))
                .findFirst()
                .map(handler -> ((NotificationStrategy<NotificationEvent>) handler).createNotification(event, user))
                .orElseThrow(() -> new IllegalArgumentException(
                        "No handler found for event type: " + event.getClass().getSimpleName()
                ));
    }
}