package com.example.taxone.dto.response;


import com.example.taxone.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Notification.NotificationType type;
}
