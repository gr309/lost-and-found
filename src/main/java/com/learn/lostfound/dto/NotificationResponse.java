
package com.learn.lostfound.dto;

import java.time.LocalDateTime;
import com.learn.lostfound.enums.NotificationType;

public class NotificationResponse {

    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Long itemId;
    private Long claimId;
    private boolean isRead;
    private LocalDateTime createdAt;

    // Default constructor
    public NotificationResponse() {
    }

    // Parameterized constructor
    public NotificationResponse(
            Long id,
            NotificationType type,
            String title,
            String message,
            Long itemId,
            Long claimId,
            boolean isRead,
            LocalDateTime createdAt) {

        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.itemId = itemId;
        this.claimId = claimId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
