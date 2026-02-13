package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.project.ProjectRoleChangedEvent;
import com.example.taxone.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

@Component
public class ProjectRoleChangedNotificationStrategy implements NotificationStrategy<ProjectRoleChangedEvent> {
    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof ProjectRoleChangedEvent;
    }

    @Override
    public Notification createNotification(ProjectRoleChangedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.PROJECT_ROLE_CHANGED)
                .isRead(false)
                .build();
    }
}
