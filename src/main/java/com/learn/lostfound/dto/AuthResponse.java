package com.learn.lostfound.dto;

public class AuthResponse {
    private AuthResponse() {}

    public record Signin(
        String accessToken, 
        String tokenType, 
        String expiresIn
    ){}
    public record UserInfo(
        Long id, 
        String name, 
        String email
    ){}
}
