package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.task.TaskAssignedEvent;
import com.example.taxone.event.task.TaskUnassignedEvent;
import com.example.taxone.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

@Component
public class TaskUnassignedNotificationStrategy implements NotificationStrategy<TaskUnassignedEvent> {
    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof TaskUnassignedEvent;
    }

    @Override
    public Notification createNotification(TaskUnassignedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.TASK_UNASSIGNED)
                .isRead(false)
                .build();
    }
}
