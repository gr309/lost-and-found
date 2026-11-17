package com.learn.lostfound.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learn.lostfound.entity.Notification;

public interface  NotificationRepository extends JpaRepository<Notification, Long>{
    
}
