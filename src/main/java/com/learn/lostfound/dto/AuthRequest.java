package com.learn.lostfound.dto;

public final class AuthRequest {
    private AuthRequest(){}

    public record Signup(
        String name, 
        String email, 
        String password
    ){}

    public record Signin(
        String email,
        String password
    ) {}
}
