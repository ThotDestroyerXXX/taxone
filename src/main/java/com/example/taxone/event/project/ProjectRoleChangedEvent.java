package com.example.taxone.event.project;

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
public class ProjectRoleChangedEvent extends NotificationEvent {

    private UUID projectId;
    private String projectName;
    private String oldRole;
    private String newRole;
    private UUID changedBy;

    public Notification.NotificationType getType() {
        return Notification.NotificationType.PROJECT_ROLE_CHANGED;
    }
}
