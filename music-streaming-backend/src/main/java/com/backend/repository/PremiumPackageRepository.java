package com.backend.repository;

import com.backend.entity.PremiumPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PremiumPackageRepository extends JpaRepository<PremiumPackage, Long> {
    
    Optional<PremiumPackage> findFirstByActiveTrue();
}
