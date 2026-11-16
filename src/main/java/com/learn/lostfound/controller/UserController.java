package com.learn.lostfound.controller;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.learn.lostfound.dto.AuthRequest;
import com.learn.lostfound.dto.AuthResponse;
import com.learn.lostfound.service.UserService;


@RestController
@RequestMapping("/api/")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("auth/signup/")
    public ResponseEntity<AuthResponse.UserInfo> signup(@RequestBody AuthRequest.Signup request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(request));
    }
    
    @PostMapping("auth/signin/")
    public ResponseEntity<AuthResponse.Signin> signin(@RequestBody AuthRequest.Signin request) {
        return ResponseEntity.ok(userService.signin(request));
    }

    @GetMapping("/user/me")
    public ResponseEntity<AuthResponse.UserInfo> me(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject()); 

        return ResponseEntity.ok(userService.getUserById(userId));
    }
}

// @RestController
// @RequestMapping("/api")
// public class UserController {

//     private final UserService userService;

//     public UserController(UserService userService) {
//         this.userService = userService;
//     }

//     @PostMapping("/auth/signup")
//     public ResponseEntity<AuthResponse.UserInfo> signup(
//         @RequestBody AuthRequest.Signup request
//     ) {
//         return ResponseEntity
//             .status(HttpStatus.CREATED)
//             .body(userService.signup(request));
//     }

//     @PostMapping("/auth/signin")
//     public ResponseEntity<AuthResponse.Signin> signin(
//         @RequestBody AuthRequest.Signin request
//     ) {
//         return ResponseEntity.ok(
//             userService.signin(request)
//         );
//     }

//     @GetMapping("/users/me")
//     public ResponseEntity<AuthResponse.UserInfo> me(
//         @AuthenticationPrincipal Jwt jwt
//     ) {
//         Long userId = Long.valueOf(jwt.getSubject());

//         return ResponseEntity.ok(
//             userService.getUserById(userId)
//         );
//     }
// }