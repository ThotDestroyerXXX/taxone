package com.example.taxone.service;

import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.event.NotificationEvent;

public interface NotificationStrategyService {
    Notification createNotification(NotificationEvent event, User user);
}
