package com.learn.lostfound.entity;

import java.time.LocalDateTime;

import com.learn.lostfound.enums.ClaimStatus;

import jakarta.persistence.*;


@Entity
@Table(name = "claims")
public class Claim {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id; 

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="item_id", nullable = false)
    private Item item; 
    

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="user_id", nullable = false)
    private User user; 
    
    @Column(columnDefinition = "TEXT")
    private String description; 

    @Enumerated (EnumType.STRING)
    @Column (nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING; 

    @Column (nullable = false, updatable = false)
    private LocalDateTime createdAt; 

    public Claim() {}

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // GETTERS AND SETTERS
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    public void setStatus(ClaimStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
} 
