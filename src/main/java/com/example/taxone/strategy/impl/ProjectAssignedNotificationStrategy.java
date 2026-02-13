package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.project.ProjectAssignedEvent;
import com.example.taxone.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

@Component
public class ProjectAssignedNotificationStrategy implements NotificationStrategy<ProjectAssignedEvent> {

    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof ProjectAssignedEvent;
    }

    @Override
    public Notification createNotification(ProjectAssignedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.PROJECT_ASSIGNED)
                .isRead(false)
                .build();
    }
}