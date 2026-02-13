package com.example.taxone.mapper;

import com.example.taxone.dto.response.NotificationResponse;
import com.example.taxone.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "notificationType", target = "type")
    @Mapping(source = "createdAt", target = "createdAt")
    NotificationResponse toResponse(Notification notification);

    @Mapping(source = "type", target = "notificationType")
    @Mapping(source = "createdAt", target = "createdAt")
    Notification toEntity(NotificationResponse notificationResponse);

    default List<NotificationResponse> toResponseList(List<Notification> notifications) {
        if(notifications == null) return List.of();

        return notifications.stream()
                .map(this::toResponse)
                .toList();
    }
}
