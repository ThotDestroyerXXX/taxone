package com.example.taxone.service;

import com.example.taxone.event.NotificationEvent;

public interface EventPublisherService {
    void publishNotificationEvent(NotificationEvent event);
}