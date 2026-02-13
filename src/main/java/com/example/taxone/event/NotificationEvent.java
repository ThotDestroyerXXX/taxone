package com.example.taxone.event;

import com.example.taxone.event.project.ProjectAssignedEvent;
import com.example.taxone.event.task.TaskAssignedEvent;
import com.example.taxone.event.task.TaskCompletedEvent;
import com.example.taxone.event.task.TaskStatusChangedEvent;
import com.example.taxone.event.workspace.WorkspaceInvitedEvent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = false
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TaskAssignedEvent.class, name = "TASK_ASSIGNED"),
        @JsonSubTypes.Type(value = TaskStatusChangedEvent.class, name = "TASK_STATUS_CHANGED"),
        @JsonSubTypes.Type(value = TaskCompletedEvent.class, name = "TASK_COMPLETED"),
        @JsonSubTypes.Type(value = ProjectAssignedEvent.class, name = "PROJECT_ASSIGNED"),
        @JsonSubTypes.Type(value = WorkspaceInvitedEvent.class, name = "WORKSPACE_INVITED"),
        // Add all event types here...
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class NotificationEvent {
    private UUID userId;
    private String message;
    private LocalDateTime timestamp;
}