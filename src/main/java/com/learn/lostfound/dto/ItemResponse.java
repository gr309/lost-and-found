package com.learn.lostfound.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.learn.lostfound.enums.ItemType;

public record ItemResponse(
    Long id, 
    String title, 
    String description, 
    ItemType type, 
    String location, 
    LocalDate eventDate, 
    String imageUrl, 
    LocalDateTime createdAt
) {}
