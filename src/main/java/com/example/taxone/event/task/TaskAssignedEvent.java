package com.example.taxone.event.task;

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
public class TaskAssignedEvent extends NotificationEvent {
    private UUID taskId;
    private String taskTitle;
    private String taskKey;
    private UUID assignedBy;

    public Notification.NotificationType getType() {
        return Notification.NotificationType.TASK_ASSIGNED;
    }
}