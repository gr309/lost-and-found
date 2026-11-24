package com.learn.lostfound.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.learn.lostfound.enums.ItemEnums;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ItemDTO {
    private ItemDTO(){}

    public static final class Request {
        private Request() {}

        public record Save(
            @NotNull Long categoryId, 
            @NotNull ItemEnums.Type type, 
            @NotBlank String title, 
            @NotBlank String description, 
            String privateDescription, 
            @NotNull LocalDate eventDate, 
            LocalTime eventTime, 
            @PositiveOrZero Integer timeMarginMinutes
        ) {}
        public record Search() {}

        public record Location(
            @NotNull Long itemId, 
            String division, 
            String district, 
            String upazila, 
            String area, 
            String addressText, 
            BigDecimal latitude, 
            BigDecimal longitude, 
            ItemEnums.LocationPrecision precision, 
            @PositiveOrZero Integer uncertaintyRadiusMeters
        ) {}
    }

    public static final class Response {
        private Response() {}
        
        // For item listings and search results
        public record Summary(
            Long id,
            ItemEnums.Type type,
            String title,
            Long categoryId,
            ItemEnums.Status status,
            LocalDate eventDate,
            LocalDateTime createdAt
        ) {}

        // For viewing a specific item
        public record Detail(
            Long id,
            Long reporterId,
            Long categoryId,
            ItemEnums.Type type,
            String title,
            String description,
            ItemEnums.Status status,
            LocalDate eventDate,
            LocalTime eventTime,
            Integer timeMarginMinutes,
            LocalDateTime createdAt
        ) {}

        // For viewing your own submitted report
        public record OwnerDetail(
            Long id,
            Long reporterId,
            Long categoryId,
            ItemEnums.Type type,
            String title,
            String description,
            String privateDescription,
            ItemEnums.Status status,
            LocalDate eventDate,
            LocalTime eventTime,
            Integer timeMarginMinutes,
            LocalDateTime createdAt
        ) {}

    }
}
