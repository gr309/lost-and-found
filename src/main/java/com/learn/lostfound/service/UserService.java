package com.learn.lostfound.service;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.learn.lostfound.dto.SignupRequest;
import com.learn.lostfound.entity.User;
import com.learn.lostfound.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Pbkdf2PasswordEncoder passwordEncoder =
            Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signup(SignupRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));

        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            // The unique constraint also protects concurrent signup requests.
            if (userRepository.existsByEmail(email)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered", exception);
            }
            throw exception;
        }
    }
}
