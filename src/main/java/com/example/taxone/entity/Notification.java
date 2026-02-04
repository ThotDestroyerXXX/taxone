package com.example.taxone.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "notifications")
public class Notification {

    public enum NotificationType {
        // Task-related
        TASK_ASSIGNED,
        TASK_UNASSIGNED,
        TASK_STATUS_CHANGED,
        TASK_PRIORITY_CHANGED,
        TASK_DUE_DATE_CHANGED,
        TASK_COMPLETED,
        TASK_OVERDUE,
        TASK_DUE_SOON,

        // Project-related
        PROJECT_ASSIGNED,        // Added as project member
        PROJECT_REMOVED,         // Removed from project
        PROJECT_ROLE_CHANGED,    // Your role in project changed

        // Workspace-related
        WORKSPACE_INVITED,       // Invited to workspace
        WORKSPACE_REMOVED,       // Removed from workspace
        WORKSPACE_ROLE_CHANGED,  // Your workspace role changed


    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false,  updatable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",  nullable = false, updatable = false)
    private User user;

    @Column(name = "message",  nullable = false, updatable = false)
    private String message;

    @Column(name = "is_read",  nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type",  nullable = false, updatable = false)
    private NotificationType notificationType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at", nullable = true)
    private LocalDateTime readAt;
}
