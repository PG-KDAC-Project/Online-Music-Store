package com.backend.service.impl;

import com.backend.dto.request.PremiumPackageRequest;
import com.backend.dto.response.PremiumPackageResponse;
import com.backend.dto.response.PremiumSubscriptionResponse;
import com.backend.entity.PremiumPackage;
import com.backend.entity.PremiumSubscription;
import com.backend.entity.User;
import com.backend.exception.ResourceNotFoundException;
import com.backend.repository.PremiumPackageRepository;
import com.backend.repository.PremiumSubscriptionRepository;
import com.backend.repository.UserRepository;
import com.backend.service.PremiumService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumServiceImpl implements PremiumService {
    
    private final PremiumPackageRepository packageRepository;
    private final PremiumSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public PremiumPackageResponse setPremiumPackage(PremiumPackageRequest request) {
        packageRepository.findFirstByActiveTrue().ifPresent(pkg -> {
            pkg.setActive(false);
            packageRepository.save(pkg);
        });
        
        PremiumPackage premiumPackage = PremiumPackage.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .durationDays(request.getDurationDays())
                .active(true)
                .build();
        
        premiumPackage = packageRepository.save(premiumPackage);
        
        return mapToResponse(premiumPackage);
    }
    
    @Override
    public PremiumPackageResponse getPremiumPackage() {
        PremiumPackage premiumPackage = packageRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No premium package available"));
        
        return mapToResponse(premiumPackage);
    }
    
    @Override
    @Transactional
    public PremiumSubscriptionResponse purchasePremium(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        PremiumPackage premiumPackage = packageRepository.findFirstByActiveTrue()
                .orElseThrow(() -> new ResourceNotFoundException("No premium package available"));
        
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
        
        return mapToSubscriptionResponse(subscription);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PremiumSubscriptionResponse getUserSubscription(Long userId) {
        PremiumSubscription subscription = subscriptionRepository
                .findActiveSubscriptionByUserId(userId, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found"));
        
        return mapToSubscriptionResponse(subscription);
    }
    
    private PremiumPackageResponse mapToResponse(PremiumPackage premiumPackage) {
        return modelMapper.map(premiumPackage, PremiumPackageResponse.class);
    }
    
    private PremiumSubscriptionResponse mapToSubscriptionResponse(PremiumSubscription subscription) {
        PremiumSubscriptionResponse response = modelMapper.map(subscription, PremiumSubscriptionResponse.class);
        response.setUserId(subscription.getUser().getId());
        response.setUserName(subscription.getUser().getName());
        return response;
    }
}
