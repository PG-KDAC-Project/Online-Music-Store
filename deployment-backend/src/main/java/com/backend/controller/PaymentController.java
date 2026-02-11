package com.backend.controller;

import com.backend.dto.request.PaymentRequest;
import com.backend.dto.request.PaymentVerificationRequest;
import com.backend.dto.response.PaymentResponse;
import com.backend.dto.response.PremiumSubscriptionResponse;
import com.backend.entity.User;
import com.backend.exception.UnauthorizedException;
import com.backend.repository.UserRepository;
import com.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    
    @PostMapping("/create-order")
    @PreAuthorize("hasRole('LISTENER')")
    public ResponseEntity<PaymentResponse> createPaymentOrder(
            @RequestBody PaymentRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        logger.info("Create payment order request from user ID: {}", userId);
        
        String customerName = request.getCustomerName() != null ? request.getCustomerName() : user.getName();
        String customerEmail = request.getCustomerEmail() != null ? request.getCustomerEmail() : user.getEmail();
        String customerPhone = request.getCustomerPhone() != null ? request.getCustomerPhone() : "9999999999";
        
        PaymentResponse response = paymentService.createPaymentOrder(
                userId,
                customerName,
                customerEmail,
                customerPhone
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/verify")
    @PreAuthorize("hasRole('LISTENER')")
    public ResponseEntity<PremiumSubscriptionResponse> verifyPayment(
            @RequestBody PaymentVerificationRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        logger.info("Verify payment request from user ID: {} for order: {}", userId, request.getOrderId());
        PremiumSubscriptionResponse response = paymentService.verifyAndActivatePremium(
                userId,
                request.getOrderId()
        );
        return ResponseEntity.ok(response);
    }
    
    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return user.getId();
    }
}
