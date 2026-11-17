package com.learn.lostfound.service;

import org.springframework.stereotype.Service;

import com.learn.lostfound.entity.Notification;
import com.learn.lostfound.entity.User;
import com.learn.lostfound.enums.NotificationType;
import com.learn.lostfound.repository.NotificationRepository;

@Service 
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(
            User user,
            NotificationType type,
            String title,
            String message,
            Long itemId,
            Long claimId) {

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setItemId(itemId);
        notification.setClaimId(claimId);

        notificationRepository.save(notification);
    }
}