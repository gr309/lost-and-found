
package com.learn.lostfound.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.lostfound.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Get all notifications of a user (newest first)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Count unread notifications of a user
    long countByUserIdAndIsReadFalse(Long userId);

    // Find a notification belonging to a specific user
    Optional<Notification> findByIdAndUserId(Long id, Long userId);
}
