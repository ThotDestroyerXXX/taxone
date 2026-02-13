package com.example.taxone.strategy;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;

public interface NotificationStrategy<T extends NotificationEvent> {

    /**
     * Check if this handler can handle the given event
     */
    boolean canHandle(NotificationEvent event);

    /**
     * Create notification from event
     */
    Notification createNotification(T event, User user);
}
