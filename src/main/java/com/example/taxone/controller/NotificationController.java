package com.example.taxone.controller;


import com.example.taxone.dto.response.NotificationResponse;
import com.example.taxone.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService  notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        return ResponseEntity.ok(notificationService.getNotifications());
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadNotificationsCount() {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsCount());
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable String notificationId) {
        return ResponseEntity.ok(notificationService.getNotification(notificationId));
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<Integer> markAllRead() {
        return ResponseEntity.ok(notificationService.markAllRead());
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear-all")
    public ResponseEntity<Void> clearAllNotifications() {
        notificationService.clearAllNotifications();
        return ResponseEntity.noContent().build();
    }
}
