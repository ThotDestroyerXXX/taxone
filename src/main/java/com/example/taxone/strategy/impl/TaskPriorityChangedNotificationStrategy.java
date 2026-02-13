package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.task.TaskPriorityChangedEvent;
import com.example.taxone.event.task.TaskStatusChangedEvent;
import com.example.taxone.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

@Component
public class TaskPriorityChangedNotificationStrategy implements NotificationStrategy<TaskPriorityChangedEvent> {

    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof TaskPriorityChangedEvent;
    }

    @Override
    public Notification createNotification(TaskPriorityChangedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.TASK_PRIORITY_CHANGED)
                .isRead(false)
                .build();
    }
}