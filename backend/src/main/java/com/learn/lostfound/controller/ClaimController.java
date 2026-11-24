package com.learn.lostfound.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import com.learn.lostfound.dto.ClaimRequest;
import com.learn.lostfound.dto.ClaimResponse;
// import com.learn.lostfound.service.ClaimService;

import jakarta.validation.Valid;

// @RestController
// @RequestMapping("/api/claims/")
// public class ClaimController {
//     private final ClaimService claimService;

//     public ClaimController(ClaimService claimService) {
//         this.claimService = claimService;
//     }

//     @PostMapping("/")
//     @ResponseStatus(HttpStatus.CREATED)
//     public ClaimResponse createClaim(@Valid @RequestBody ClaimRequest request, @AuthenticationPrincipal Jwt jwt) {
//         Long userId = Long.valueOf(jwt.getSubject());
//         return claimService.createClaim(request, userId);
//     }

//     @GetMapping("/")
//     public List<ClaimResponse> getMyClaims(@AuthenticationPrincipal Jwt jwt) {
//         Long userId = Long.valueOf(jwt.getSubject());
//         return claimService.getMyClaims(userId);
//     }

//     @PostMapping("{id}/approve/")
//     public ClaimResponse approveClaim(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
//         Long userId = Long.valueOf(jwt.getSubject());
//         return claimService.approveClaim(id, userId);
//     }

//     @PostMapping("{id}/reject/")
//     public ClaimResponse rejectClaim(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
//         Long userId = Long.valueOf(jwt.getSubject());
//         return claimService.rejectClaim(id, userId);
//     }
// }
