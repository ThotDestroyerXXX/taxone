package com.example.taxone.strategy.impl;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;
import com.example.taxone.event.workspace.WorkspaceInvitedEvent;
import com.example.taxone.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceInvitedNotificationStrategy implements NotificationStrategy<WorkspaceInvitedEvent> {

    @Override
    public boolean canHandle(NotificationEvent event) {
        return event instanceof WorkspaceInvitedEvent;
    }

    @Override
    public Notification createNotification(WorkspaceInvitedEvent event, User user) {
        return Notification.builder()
                .user(user)
                .message(event.getMessage())
                .notificationType(Notification.NotificationType.WORKSPACE_INVITED)
                .isRead(false)
                .build();
    }
}