package com.backend.service;

import com.backend.dto.request.LoginRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.response.AuthResponse;

/**
 * Authentication Service Interface
 * 
 * Handles user authentication operations including registration and login.
 * 
 * @author Music Streaming Team
 * @since 1.0
 */
public interface AuthService {
    
    /**
     * Register a new user
     * 
     * Creates a new user account with the provided information.
     * - LISTENER role: Auto-approved
     * - ARTIST role: Requires admin approval (approved = false)
     * - ADMIN role: Auto-approved (restricted in production)
     * 
     * @param request registration details
     * @return authentication response with JWT token
     * @throws DuplicateResourceException if email already exists
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * Authenticate user and generate JWT token
     * 
     * Validates user credentials and generates a JWT token for successful authentication.
     * 
     * @param request login credentials
     * @return authentication response with JWT token
     * @throws BadCredentialsException if credentials are invalid
     * @throws DisabledException if account is disabled
     */
    AuthResponse login(LoginRequest request);
}
