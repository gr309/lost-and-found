package com.learn.lostfound.service;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.learn.lostfound.dto.AuthRequest;
import com.learn.lostfound.dto.AuthResponse;
import com.learn.lostfound.entity.User;
import com.learn.lostfound.repository.UserRepository;
// import com.learn.lostfound.service.JwtService;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; 
    private final JwtService jwtService; 

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; 
        this.jwtService = jwtService;         
    }

    public AuthResponse.UserInfo signup(AuthRequest.Signup request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if(userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "Email already exists"   
            ); 
        }

        User user = new User(); 

        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));

        try {
            User savedUser = userRepository.saveAndFlush(user); 
            return new AuthResponse.UserInfo(
                savedUser.getId(), 
                savedUser.getName(), 
                savedUser.getEmail()
            ); 
        }
        catch (DataIntegrityViolationException exception) {
            if (userRepository.existsByEmail(email)) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered",
                    exception
                );
            }
            throw exception;       
        }

    }

    public AuthResponse.Signin signin(AuthRequest.Signin request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> 
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, 
                    "Invalid email or password"
                )
        ); 

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user); 
        return new AuthResponse.Signin(token, "Bearer", "30 days"); 
    }

    public AuthResponse.UserInfo getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> 
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "User not found"
                )
        );

        return new AuthResponse.UserInfo(
            user.getId(), 
            user.getName(), 
            user.getEmail()
        );

    }

    // public String sigin(SigninRequest request) {
    //     User user = userRepository.findByEmail(request.email()).orElseThrow(() -> 
    //         new ResponseStatusException(
    //             HttpStatus.UNAUTHORIZED, 
    //             "Invalid emailor password"
    //         )
    //     );
    //     if(!passwordEncoder.matches(request.password(), user.getPassword())) {
    //         throw new ResponseStatusException(
    //             HttpStatus.UNAUTHORIZED, 
    //             "Invalid emailor password"
    //         );
    //     }

    //     return "Signin Success";
    // }
}
