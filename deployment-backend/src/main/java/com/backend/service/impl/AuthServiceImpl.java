package com.backend.service.impl;

import com.backend.dto.request.LoginRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.response.AuthResponse;
import com.backend.entity.Role;
import com.backend.entity.User;
import com.backend.exception.DuplicateResourceException;
import com.backend.repository.UserRepository;
import com.backend.security.JwtUtil;
import com.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Authentication Service Implementation
 * 
 * Implements user authentication operations including:
 * - User registration (LISTENER auto-approved, ARTIST pending approval)
 * - User login with JWT token generation
 * - Password encryption with BCrypt
 * - Meaningful exception handling
 * 
 * @author Music Streaming Team
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Register a new user
     * 
     * Registration Flow:
     * 1. Check if email already exists
     * 2. Determine role (default to LISTENER if not provided)
     * 3. Encrypt password with BCrypt
     * 4. Create user entity
     * 5. Save to database (approved status set by @PrePersist)
     * 6. Generate JWT token
     * 7. Return authentication response
     * 
     * Role-based Approval:
     * - LISTENER: Auto-approved (approved = true)
     * - ARTIST: Requires approval (approved = false)
     * - ADMIN: Auto-approved (approved = true)
     * 
     * @param request registration details (name, email, password, role)
     * @return authentication response with JWT token and role
     * @throws DuplicateResourceException if email already registered
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registration attempt for email: {}", request.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new DuplicateResourceException(
                "Email already registered: " + request.getEmail()
            );
        }
        
        // Determine role (default to LISTENER if not provided)
        Role role = request.getRole() != null ? request.getRole() : Role.LISTENER;
        
        // Encrypt password with BCrypt
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        
        // Create user entity
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encryptedPassword)
                .role(role)
                .enabled(true)
                .approved(false) // Will be set by @PrePersist based on role
                .build();
        
        // Save user to database
        // Note: @PrePersist will automatically:
        // - Set createdAt timestamp
        // - Set approved = true for LISTENER and ADMIN
        // - Keep approved = false for ARTIST
        User savedUser = userRepository.save(user);
        
        logger.info("User registered successfully: {} with role: {}, approved: {}", 
                    savedUser.getEmail(), savedUser.getRole(), savedUser.getApproved());
        
        // Generate JWT token
        Authentication auth = new UsernamePasswordAuthenticationToken(
            savedUser.getEmail(),
            null,
            List.of(() -> savedUser.getRole().getAuthority())
        );
        String token = jwtUtil.createToken(auth);
        
        // Return authentication response
        return AuthResponse.builder()
                .token(token)
                .role(savedUser.getRole().getAuthority())
                .premium(savedUser.getPremium())
                .build();
    }
    
    /**
     * Authenticate user and generate JWT token
     * 
     * Login Flow:
     * 1. Authenticate credentials using Spring Security
     * 2. Load user from database
     * 3. Verify account is active
     * 4. Generate JWT token
     * 5. Return authentication response
     * 
     * @param request login credentials (email, password)
     * @return authentication response with JWT token and role
     * @throws BadCredentialsException if email or password is incorrect
     * @throws DisabledException if account is disabled
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        
        try {
            // Authenticate user credentials
            // This will throw BadCredentialsException if credentials are invalid
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            logger.debug("Authentication successful for: {}", request.getEmail());
            
        } catch (BadCredentialsException e) {
            logger.warn("Login failed: Invalid credentials for email - {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
            
        } catch (DisabledException e) {
            logger.warn("Login failed: Account disabled for email - {}", request.getEmail());
            throw new DisabledException("Account is disabled. Please contact support.");
        }
        
        // Load user from database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found after successful authentication: {}", request.getEmail());
                    return new UsernameNotFoundException("User not found: " + request.getEmail());
                });
        
        // Additional check: Verify account is active
        if (!user.isActive()) {
            logger.warn("Login failed: Account is not active - {}", request.getEmail());
            throw new DisabledException("Account is disabled. Please contact support.");
        }
        
        // Generate JWT token
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user.getEmail(),
            null,
            List.of(() -> user.getRole().getAuthority())
        );
        String token = jwtUtil.createToken(auth);
        
        logger.info("Login successful for: {} with role: {}", user.getEmail(), user.getRole());
        
        // Return authentication response
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().getAuthority())
                .premium(user.getPremium())
                .build();
    }
}
