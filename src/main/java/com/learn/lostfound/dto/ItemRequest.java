package com.learn.lostfound.dto;

import java.time.LocalDate;

import com.learn.lostfound.enums.ItemType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record ItemRequest (
    @NotBlank 
    @Size (max=255)
    String title, 

    @NotBlank 
    String description, 

    @NotNull 
    ItemType type, 

    @NotNull 
    Long categoryId, 
    
    @NotBlank
    String location,

    @NotNull
    @PastOrPresent
    LocalDate eventDate
){}
