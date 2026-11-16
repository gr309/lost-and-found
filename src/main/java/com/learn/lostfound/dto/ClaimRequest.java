package com.learn.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClaimRequest(

    @NotNull
    Long itemId,

    @NotBlank
    String description

) {}