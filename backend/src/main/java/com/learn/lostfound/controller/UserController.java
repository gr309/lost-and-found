package com.learn.lostfound.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learn.lostfound.dto.Auth;
import com.learn.lostfound.service.UserService;


@RestController
@RequestMapping("/api/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("auth/signup/")
    public ResponseEntity<Auth.Response.UserInfo> signup(@RequestBody Auth.Request.Signup request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(request));
    }
    
    @PostMapping("auth/signin/")
    public ResponseEntity<Auth.Response.Signin> signin(@RequestBody Auth.Request.Signin request) {
        return ResponseEntity.ok(userService.signin(request));
    }

    @GetMapping("me/")
    public ResponseEntity<Auth.Response.UserInfo> me(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject()); 

        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
