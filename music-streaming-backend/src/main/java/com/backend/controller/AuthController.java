package com.backend.controller;

import com.backend.dto.request.LoginRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.response.AuthResponse;
import com.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthService authService;
    
    /**
     * Register a new user
     * Creates a new user account with the provided information.
     * If role is not provided, defaults to LISTENER.
     *  request registration details (name, email, password, role)
     * ResponseEntity with AuthResponse (JWT token and role)
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request received for email: {}", request.getEmail());
        
        AuthResponse response = authService.register(request);
        
        logger.info("User registered successfully: {}, role: {}", 
                    request.getEmail(), response.getRole());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Authenticate user and generate JWT token
     * Validates user credentials and returns a JWT token for successful authentication.
     * request login credentials (email, password)
     *  ResponseEntity with AuthResponse (JWT token and role)
     *  BadCredentialsException if credentials are invalid (handled by GlobalExceptionHandler)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request received for email: {}", request.getEmail());
        
        AuthResponse response = authService.login(request);
        
        logger.info("User logged in successfully: {}, role: {}", 
                    request.getEmail(), response.getRole());
        
        return ResponseEntity.ok(response);
    }
}
