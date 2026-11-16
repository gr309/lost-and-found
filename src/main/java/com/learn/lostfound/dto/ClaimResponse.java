package com.learn.lostfound.dto;

import java.time.LocalDateTime;

import com.learn.lostfound.enums.ClaimStatus;

public record ClaimResponse(
    Long id,
    Long itemId,
    String description,
    ClaimStatus status,
    LocalDateTime createdAt
) {}