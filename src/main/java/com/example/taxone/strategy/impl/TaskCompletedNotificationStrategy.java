package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.task.TaskCompletedEvent;
import com.example.taxone.strategy.NotificationStrategy;

public class TaskCompletedNotificationStrategy implements NotificationStrategy<TaskCompletedEvent> {
    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof TaskCompletedEvent;
    }

    @Override
    public Notification createNotification(TaskCompletedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.TASK_COMPLETED)
                .isRead(false)
                .build();
    }
}
