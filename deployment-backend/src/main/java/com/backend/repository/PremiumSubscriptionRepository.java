package com.backend.repository;

import com.backend.entity.PremiumSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PremiumSubscriptionRepository extends JpaRepository<PremiumSubscription, Long> {
    
    @Query("SELECT ps FROM PremiumSubscription ps WHERE ps.user.id = :userId AND ps.active = true AND ps.expiresAt > :now ORDER BY ps.expiresAt DESC")
    Optional<PremiumSubscription> findActiveSubscriptionByUserId(Long userId, LocalDateTime now);
}
