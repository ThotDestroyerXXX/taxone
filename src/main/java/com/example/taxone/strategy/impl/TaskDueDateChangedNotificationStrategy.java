package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.task.TaskDueDateChangedEvent;
import com.example.taxone.event.task.TaskPriorityChangedEvent;
import com.example.taxone.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

@Component
public class TaskDueDateChangedNotificationStrategy implements NotificationStrategy<TaskDueDateChangedEvent> {

    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof TaskDueDateChangedEvent;
    }

    @Override
    public Notification createNotification(TaskDueDateChangedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.TASK_DUE_DATE_CHANGED)
                .isRead(false)
                .build();
    }
}