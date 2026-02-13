package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.workspace.WorkspaceRemovedEvent;
import com.example.taxone.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceRemovedNotificationStrategy implements NotificationStrategy<WorkspaceRemovedEvent> {

    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof WorkspaceRemovedEvent;
    }

    @Override
    public Notification createNotification(WorkspaceRemovedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.WORKSPACE_REMOVED)
                .isRead(false)
                .build();
    }
}