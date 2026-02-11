package com.backend.service;

import com.backend.dto.response.PaymentResponse;
import com.backend.dto.response.PremiumSubscriptionResponse;

public interface PaymentService {
    
    PaymentResponse createPaymentOrder(Long userId, String customerName, String customerEmail, String customerPhone);
    
    PremiumSubscriptionResponse verifyAndActivatePremium(Long userId, String orderId);
}
