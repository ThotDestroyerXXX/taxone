package com.example.taxone.mapper;

import com.example.taxone.dto.response.NotificationResponse;
import com.example.taxone.entity.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);

    Notification toEntity(NotificationResponse notificationResponse);

    default List<NotificationResponse> toResponseList(List<Notification> notifications) {
        if(notifications == null) return List.of();

        return notifications.stream()
                .map(this::toResponse)
                .toList();
    }
}
