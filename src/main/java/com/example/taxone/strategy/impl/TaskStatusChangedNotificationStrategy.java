package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.task.TaskStatusChangedEvent;
import com.example.taxone.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusChangedNotificationStrategy implements NotificationStrategy<TaskStatusChangedEvent> {

    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof TaskStatusChangedEvent;
    }

    @Override
    public Notification createNotification(TaskStatusChangedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.TASK_STATUS_CHANGED)
                .isRead(false)
                .build();
    }
}