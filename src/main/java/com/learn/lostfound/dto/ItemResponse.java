package com.learn.lostfound.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.learn.lostfound.enums.ItemType;

public record ItemResponse(
    Long id, 
    String title, 
    String description, 
    ItemType type, 
    long categoryId, 
    String categoryName, 
    String location, 
    LocalDate eventDate, 
    LocalDateTime createdAt
) {}
