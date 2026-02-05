package com.backend.controller;

import com.backend.dto.response.PremiumPackageResponse;
import com.backend.dto.response.PremiumSubscriptionResponse;
import com.backend.entity.User;
import com.backend.exception.UnauthorizedException;
import com.backend.repository.UserRepository;
import com.backend.service.PremiumService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/premium")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PremiumController {
    
    private static final Logger logger = LoggerFactory.getLogger(PremiumController.class);
    
    private final PremiumService premiumService;
    private final UserRepository userRepository;
    
    @GetMapping("/package")
    public ResponseEntity<PremiumPackageResponse> getPremiumPackage() {
        logger.debug("Get premium package request");
        PremiumPackageResponse response = premiumService.getPremiumPackage();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/purchase")
    @PreAuthorize("hasRole('LISTENER')")
    public ResponseEntity<PremiumSubscriptionResponse> purchasePremium(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        logger.info("Purchase premium request from user ID: {}", userId);
        
        PremiumSubscriptionResponse response = premiumService.purchasePremium(userId);
        
        logger.info("Premium purchased successfully by user ID: {}", userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/subscription")
    @PreAuthorize("hasRole('LISTENER')")
    public ResponseEntity<PremiumSubscriptionResponse> getUserSubscription(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        logger.debug("Get subscription request for user ID: {}", userId);
        
        PremiumSubscriptionResponse response = premiumService.getUserSubscription(userId);
        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return user.getId();
    }
}
