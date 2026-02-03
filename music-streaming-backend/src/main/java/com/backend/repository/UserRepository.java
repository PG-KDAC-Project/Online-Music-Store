package com.backend.repository;

import com.backend.entity.Role;
import com.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(Role role);
    
    List<User> findByRoleAndApprovedFalse(Role role);
    
    boolean existsByEmail(String email);
    
    long countByRole(Role role);
    
    Long countByRoleAndApprovedFalse(Role role);
    
    Long countByRoleAndApprovedTrue(Role role);
    
    Long countByEnabledFalse();
    
    Long countByPremiumTrue();
}
