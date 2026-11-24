
package com.learn.lostfound.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.learn.lostfound.dto.NotificationResponse;
import com.learn.lostfound.entity.Notification;
import com.learn.lostfound.entity.User;
import com.learn.lostfound.enums.NotificationType;
import com.learn.lostfound.repository.NotificationRepository;

// @Service
// public class NotificationService {
//     private final NotificationRepository notificationRepository;

//     public NotificationService(NotificationRepository notificationRepository) {
//         this.notificationRepository = notificationRepository;
//     }

//     // 1. Create notification
//     @Transactional
//     public void createNotification(
//             User user,
//             NotificationType type,
//             String title,
//             String message,
//             Long itemId,
//             Long claimId) {

//         Notification notification = new Notification();

//         notification.setUser(user);
//         notification.setType(type);
//         notification.setTitle(title);
//         notification.setMessage(message);
//         notification.setItemId(itemId);
//         notification.setClaimId(claimId);

//         notificationRepository.save(notification);
//     }

//     // 2. Get all notifications of a user
//     @Transactional(readOnly = true)
//     public List<NotificationResponse> getNotifications(Long userId) {

//         return notificationRepository
//                 .findByUserIdOrderByCreatedAtDesc(userId)
//                 .stream()
//                 .map(this::toResponse)
//                 .toList();
//     }

//     // 3. Count unread notifications

    
//     @Transactional(readOnly = true)
//     public long getUnreadCount(Long userId) {

//         return notificationRepository
//                 .countByUserIdAndIsReadFalse(userId);
//     }

//     // 4. Mark one notification as read
//     @Transactional
//     public NotificationResponse markAsRead(
//             Long notificationId,
//             Long userId) {

//         Notification notification = notificationRepository
//                 .findByIdAndUserId(notificationId, userId)
//                 .orElseThrow(() -> new ResponseStatusException(
//                         HttpStatus.NOT_FOUND,
//                         "Notification not found"
//                 ));

//         notification.setRead(true);

//         return toResponse(notificationRepository.save(notification));
//     }

//     // 5. Mark all notifications as read
//     @Transactional
//     public void markAllAsRead(Long userId) {

//         List<Notification> notifications = notificationRepository
//                 .findByUserIdOrderByCreatedAtDesc(userId);

//         for (Notification notification : notifications) {
//             if (!notification.isRead()) {
//                 notification.setRead(true);
//             }
//         }
//     }

//     // 6. Convert Entity to DTO
//     private NotificationResponse toResponse(
//             Notification notification) {

//         return new NotificationResponse(
//                 notification.getId(),
//                 notification.getType(),
//                 notification.getTitle(),
//                 notification.getMessage(),
//                 notification.getItemId(),
//                 notification.getClaimId(),
//                 notification.isRead(),
//                 notification.getCreatedAt()
//         );
//     }
// }
