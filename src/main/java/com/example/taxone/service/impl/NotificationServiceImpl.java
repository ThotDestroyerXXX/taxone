package com.example.taxone.service.impl;


import com.example.taxone.dto.response.NotificationResponse;
import com.example.taxone.entity.Notification;
import com.example.taxone.entity.User;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.NotificationMapper;
import com.example.taxone.repository.NotificationRepository;
import com.example.taxone.service.NotificationService;
import com.example.taxone.util.AuthenticationHelper;
import com.example.taxone.util.PermissionHelper;
import com.example.taxone.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationMapper  notificationMapper;

    private final AuthenticationHelper authenticationHelper;
    private final PermissionHelper permissionHelper;

    @Override
    public List<NotificationResponse> getNotifications() {
        User user = authenticationHelper.getCurrentUser();

        List<Notification> notifications = notificationRepository.findAllByUserId(user.getId());

        return notificationMapper.toResponseList(notifications);
    }

    @Override
    public Long getUnreadNotificationsCount() {
        User user = authenticationHelper.getCurrentUser();

        return notificationRepository.countByUserIdAndIsRead(user.getId(), false);
    }

    @Override
    public NotificationResponse getNotification(String notificationId) {
        User user = authenticationHelper.getCurrentUser();
        UUID notificationUUID = UUIDUtils.fromString(notificationId, "notification");

        Notification notification = notificationRepository.findByIdAndUserId(notificationUUID, user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found"));

        if(notification.getIsRead() == false) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        return notificationMapper.toResponse(notification);
    }

    @Override
    @Transactional
    public Integer markAllRead() {
        User user = authenticationHelper.getCurrentUser();

        return notificationRepository.markAllAsReadByUserId(user.getId());
    }

    @Override
    public void deleteNotification(String notificationId) {
        User user = authenticationHelper.getCurrentUser();
        UUID notificationUUID = UUIDUtils.fromString(notificationId, "notification");

        notificationRepository.deleteByIdAndUserId(notificationUUID, user.getId());
    }

    @Override
    public void clearAllNotifications() {
        User user = authenticationHelper.getCurrentUser();

        notificationRepository.deleteAllByUserId(user.getId());
    }
}
