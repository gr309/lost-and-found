package com.learn.lostfound.controller;

import org.springframework.web.bind.annotation.*;

import com.learn.lostfound.entity.Notification;


@RestController 
@RequestMapping("/api/notifications/")
public class NotificationController {
    @GetMapping("/")
    public Notification getNotification() {
        
    }

}
