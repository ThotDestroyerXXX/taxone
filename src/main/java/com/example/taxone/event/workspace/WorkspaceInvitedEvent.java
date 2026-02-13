package com.example.taxone.event.workspace;

import com.example.taxone.entity.Notification;
import com.example.taxone.event.NotificationEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkspaceInvitedEvent extends NotificationEvent {
    private UUID workspaceId;
    private String workspaceName;
    private String role;
    private UUID invitedBy;

    public Notification.NotificationType getType() {
        return Notification.NotificationType.WORKSPACE_INVITED;
    }
}