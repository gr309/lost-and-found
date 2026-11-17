package com.learn.lostfound.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
            "application", "Lost & Found",
            "status", "running",
            "version", "1.0"
        );
    }

    @GetMapping("/health/")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}