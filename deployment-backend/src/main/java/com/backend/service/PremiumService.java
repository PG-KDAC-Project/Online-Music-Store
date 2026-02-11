package com.backend.service;

import com.backend.dto.request.PremiumPackageRequest;
import com.backend.dto.response.PremiumPackageResponse;
import com.backend.dto.response.PremiumSubscriptionResponse;

public interface PremiumService {
    
    PremiumPackageResponse setPremiumPackage(PremiumPackageRequest request);
    
    PremiumPackageResponse getPremiumPackage();
    
    PremiumSubscriptionResponse purchasePremium(Long userId);
    
    PremiumSubscriptionResponse getUserSubscription(Long userId);
}
