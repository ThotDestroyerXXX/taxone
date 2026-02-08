package com.example.taxone.repository;

import com.example.taxone.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByUserId(UUID userId);
    Long countByUserIdAndIsRead(UUID userId, boolean isRead);
    Optional<Notification> findByIdAndUserId(UUID notificationUUID, UUID id);

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.isRead = true,
            n.readAt = CURRENT_TIMESTAMP
        WHERE n.user.id = :userId
          AND n.isRead = false
    """)
    Integer markAllAsReadByUserId(@Param("userId") UUID userId);

    void deleteByIdAndUserId(UUID notificationUUID, UUID id);
    void deleteAllByUserId(UUID id);
}
