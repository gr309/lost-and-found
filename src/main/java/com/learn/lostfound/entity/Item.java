package com.learn.lostfound.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.learn.lostfound.enums.ItemType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;


@Entity 
@Table (name="items")
public class Item {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id; 
    
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="user_id", nullable = false)
    private User user; 
    
    @Column (nullable=false)
    private String title; 

    @Column (nullable = false, columnDefinition = "TExT")
    private String description; 

    @Enumerated (EnumType.STRING)
    @Column (nullable = false)
    private ItemType type; 

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDate eventDate;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name="image_url")
    private String imageUrl; 

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public Item() {}

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
