package com.learn.lostfound.entity;

import java.time.LocalDateTime;

import javax.swing.Spring;

import com.learn.lostfound.enums.ItemType;

import jakarta.persistence.*;


@Entity 
@Table (name="items")
public class Item {
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id; 
    
    @Column (nullable=false)
    private String title; 

    @Column (nullable = false, columnDefinition = "TExT")
    private String description; 

    @Enumerated (EnumType.STRING)
    @Column (nullable = false)
    private ItemType type; 

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name="category_id", nullable = false)
    private Category category; 

    @Column(nullable = false)
    private Spring location;

    @Column (nullable = false, updatable = false)
    private LocalDateTime createdAt; 

    public Item() {}

    @PrePersist 
    public void onCreate() {
        createdAt = LocalDateTime.now(); 
    }
}
