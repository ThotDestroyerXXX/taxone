package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.workspace.WorkspaceRemovedEvent;
import com.example.taxone.event.workspace.WorkspaceRoleChangedEvent;
import com.example.taxone.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceRoleChangedNotificationStrategy implements NotificationStrategy<WorkspaceRoleChangedEvent> {

    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof WorkspaceRoleChangedEvent;
    }

    @Override
    public Notification createNotification(WorkspaceRoleChangedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.WORKSPACE_ROLE_CHANGED)
                .isRead(false)
                .build();
    }
}