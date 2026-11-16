package com.learn.lostfound.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.learn.lostfound.dto.SignupRequest;
import com.learn.lostfound.entity.User;
import com.learn.lostfound.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("signup/")
    @ResponseStatus(HttpStatus.CREATED)
    public User signup(@Valid @RequestBody SignupRequest request) {
        return userService.signup(request);
    }
}