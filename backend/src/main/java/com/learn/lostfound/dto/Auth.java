package com.learn.lostfound.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public final class Auth {
    private Auth() {}
    
    public static final class Request {
        private Request(){}

        public record Signup(
            @NotBlank String name, 
            @NotBlank String email, 
            @NotBlank String password, 
            String phone
        ) {}

        public record Signin(
            @NotBlank String email, 
            @NotBlank String password
        ) {}
    }


    public static final class Response {
        private Response(){}
        
        public record Signin(
            Long userId,
            String accessToken
        ) {}

        public record UserInfo(
            Long id, 
            String name, 
            String email, 
            String phone, 
            String role, 
            String status, 
            LocalDateTime created_at, 
            LocalDateTime updated_at
        ){}

    }
}
