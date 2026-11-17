package com.learn.lostfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.learn.lostfound.entity.Notification;
import com.learn.lostfound.entity.User;
import java.util.Optional;


public interface  NotificationRepository extends JpaRepository<Notification, Long>{
    Optional <User> findByUser(User user); 
    Optional <User> getUnreadNotification(User user, boolean isRead);
}
