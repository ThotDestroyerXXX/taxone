package com.example.taxone.event.workspace;

import com.example.taxone.entity.Notification;
import com.example.taxone.event.NotificationEvent;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkspaceRoleChangedEvent extends NotificationEvent {

    private UUID workspaceId;
    private String workspaceName;
    private String oldRole;
    private String newRole;

    public Notification.NotificationType getType() {
        return Notification.NotificationType.WORKSPACE_ROLE_CHANGED;
    }
}
