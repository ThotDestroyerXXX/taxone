package com.example.taxone.service;

import com.example.taxone.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getNotifications();
    Long getUnreadNotificationsCount();
    NotificationResponse getNotification(String notificationId);
    Integer markAllRead();
    void deleteNotification(String notificationId);
    void clearAllNotifications();
}
