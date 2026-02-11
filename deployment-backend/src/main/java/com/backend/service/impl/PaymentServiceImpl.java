package com.backend.service.impl;

import com.backend.dto.response.PaymentResponse;
import com.backend.dto.response.PremiumSubscriptionResponse;
import com.backend.entity.PremiumPackage;
import com.backend.entity.PremiumSubscription;
import com.backend.entity.User;
import com.backend.exception.BadRequestException;
import com.backend.exception.ResourceNotFoundException;
import com.backend.repository.PremiumPackageRepository;
import com.backend.repository.PremiumSubscriptionRepository;
import com.backend.repository.UserRepository;
import com.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    
    private final PremiumPackageRepository packageRepository;
    private final PremiumSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final WebClient.Builder webClientBuilder;
    private final ModelMapper modelMapper;
    
    @Value("${cashfree.app.id}")
    private String appId;
    
    @Value("${cashfree.secret.key}")
    private String secretKey;
    
    @Value("${cashfree.api.version}")
    private String apiVersion;
    
    @Value("${cashfree.environment}")
    private String environment;
    
    private String getBaseUrl() {
        return environment.equalsIgnoreCase("production") 
                ? "https://api.cashfree.com/pg" 
                : "https://sandbox.cashfree.com/pg";
    }
    
    @Override
    @Transactional
    public PaymentResponse createPaymentOrder(Long userId, String customerName, String customerEmail, String customerPhone) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            PremiumPackage premiumPackage = packageRepository.findFirstByActiveTrue()
                    .orElseThrow(() -> new ResourceNotFoundException("No premium package available"));
            
            String orderId = "ORDER_" + UUID.randomUUID().toString();
            
            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("order_id", orderId);
            orderRequest.put("order_amount", premiumPackage.getPrice().doubleValue());
            orderRequest.put("order_currency", "INR");
            
            Map<String, String> customerDetails = new HashMap<>();
            customerDetails.put("customer_id", "CUST_" + user.getId());
            customerDetails.put("customer_name", customerName);
            customerDetails.put("customer_email", customerEmail);
            customerDetails.put("customer_phone", customerPhone);
            orderRequest.put("customer_details", customerDetails);
            
            Map<String, String> orderMeta = new HashMap<>();
            orderMeta.put("return_url", "http://localhost:3000/payment/success");
            orderRequest.put("order_meta", orderMeta);
            
            logger.info("Creating Cashfree order: {}", orderRequest);
            
            WebClient webClient = webClientBuilder
                    .baseUrl(getBaseUrl())
                    .defaultHeader("x-client-id", appId)
                    .defaultHeader("x-client-secret", secretKey)
                    .defaultHeader("x-api-version", apiVersion)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            
            Map<String, Object> response = webClient.post()
                    .uri("/orders")
                    .bodyValue(orderRequest)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        logger.error("Cashfree API error response: {}", errorBody);
                                        return new BadRequestException("Cashfree API error: " + errorBody);
                                    }))
                    .bodyToMono(Map.class)
                    .block();
            
            logger.info("Payment order created: {} for user: {}", orderId, userId);
            
            String paymentSessionId = response != null ? (String) response.get("payment_session_id") : orderId;
            
            return PaymentResponse.builder()
                    .orderId(orderId)
                    .paymentSessionId(paymentSessionId)
                    .amount(premiumPackage.getPrice().doubleValue())
                    .currency("INR")
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error creating payment order: {}", e.getMessage());
            throw new BadRequestException("Failed to create payment order: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public PremiumSubscriptionResponse verifyAndActivatePremium(Long userId, String orderId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            PremiumPackage premiumPackage = packageRepository.findFirstByActiveTrue()
                    .orElseThrow(() -> new ResourceNotFoundException("No premium package available"));
            
            // For testing: Skip Cashfree verification if credentials are not set
            boolean skipVerification = "YOUR_APP_ID".equals(appId) || "YOUR_SECRET_KEY".equals(secretKey);
            
            if (!skipVerification) {
                WebClient webClient = webClientBuilder
                        .baseUrl(getBaseUrl())
                        .defaultHeader("x-client-id", appId)
                        .defaultHeader("x-client-secret", secretKey)
                        .defaultHeader("x-api-version", apiVersion)
                        .build();
                
                Map<String, Object> response = webClient.get()
                        .uri("/orders/" + orderId)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();
                
                String orderStatus = (String) response.get("order_status");
                
                if (!"PAID".equals(orderStatus)) {
                    throw new BadRequestException("Payment not completed");
                }
            } else {
                logger.warn("Skipping Cashfree verification - using test mode");
            }
            
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiresAt = now.plusDays(premiumPackage.getDurationDays());
            
            PremiumSubscription subscription = PremiumSubscription.builder()
                    .user(user)
                    .amountPaid(premiumPackage.getPrice())
                    .durationDays(premiumPackage.getDurationDays())
                    .purchasedAt(now)
                    .expiresAt(expiresAt)
                    .active(true)
                    .build();
            
            subscription = subscriptionRepository.save(subscription);
            
            user.setPremium(true);
            userRepository.save(user);
            
            logger.info("Premium activated for user: {} with order: {}", userId, orderId);
            
            return mapToSubscriptionResponse(subscription);
            
        } catch (Exception e) {
            logger.error("Error verifying payment: {}", e.getMessage());
            throw new BadRequestException("Failed to verify payment: " + e.getMessage());
        }
    }
    
    private PremiumSubscriptionResponse mapToSubscriptionResponse(PremiumSubscription subscription) {
        PremiumSubscriptionResponse response = modelMapper.map(subscription, PremiumSubscriptionResponse.class);
        response.setUserId(subscription.getUser().getId());
        response.setUserName(subscription.getUser().getName());
        return response;
    }
}
