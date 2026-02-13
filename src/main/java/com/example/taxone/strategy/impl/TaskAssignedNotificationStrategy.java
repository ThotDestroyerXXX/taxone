package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.task.TaskAssignedEvent;
import com.example.taxone.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

@Component
public class TaskAssignedNotificationStrategy implements NotificationStrategy<TaskAssignedEvent> {
    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof TaskAssignedEvent;
    }

    @Override
    public Notification createNotification(TaskAssignedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.TASK_ASSIGNED)
                .isRead(false)
                .build();
    }
}
