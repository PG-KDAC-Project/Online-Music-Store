package com.backend.service.impl;

import com.backend.dto.request.UserStatusUpdateRequest;
import com.backend.dto.response.UserResponse;
import com.backend.entity.Role;
import com.backend.entity.User;
import com.backend.exception.ResourceNotFoundException;
import com.backend.repository.UserRepository;
import com.backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    // ========== User Management ==========
    
    @Override
    public UserResponse approveArtist(Long artistId) {
        logger.info("Approving artist ID: {}", artistId);
        
        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", artistId));
        
        if (user.getRole() != Role.ARTIST) {
            throw new IllegalArgumentException("User is not an artist. Cannot approve.");
        }
        
        user.setApproved(true);
        User updatedUser = userRepository.save(user);
        
        logger.info("Artist approved successfully: ID {}", artistId);
        return mapToUserResponse(updatedUser);
    }
    
    @Override
    public UserResponse rejectArtist(Long artistId) {
        logger.info("Rejecting artist ID: {}", artistId);
        
        User user = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", artistId));
        
        user.setApproved(false);
        User updatedUser = userRepository.save(user);
        
        logger.info("Artist rejected: ID {}", artistId);
        return mapToUserResponse(updatedUser);
    }
    
    @Override
    public UserResponse suspendUser(Long userId, UserStatusUpdateRequest request) {
        logger.info("Suspending user ID: {}. Reason: {}", userId, request.getReason());
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        user.setEnabled(false);
        User updatedUser = userRepository.save(user);
        
        logger.info("User suspended successfully: ID {}", userId);
        return mapToUserResponse(updatedUser);
    }
    
    @Override
    public UserResponse activateUser(Long userId) {
        logger.info("Activating user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        user.setEnabled(true);
        User updatedUser = userRepository.save(user);
        
        logger.info("User activated successfully: ID {}", userId);
        return mapToUserResponse(updatedUser);
    }
    
    // ========== User Listings ==========
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        logger.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(String role) {
        logger.debug("Fetching users by role: {}", role);
        Role userRole = Role.valueOf(role.toUpperCase());
        return userRepository.findByRole(userRole).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getPendingArtists() {
        logger.debug("Fetching pending artists");
        return userRepository.findByRoleAndApprovedFalse(Role.ARTIST).stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object getUserStatistics() {
        logger.debug("Calculating user statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", userRepository.count());
        stats.put("totalListeners", userRepository.countByRole(Role.LISTENER));
        stats.put("totalArtists", userRepository.countByRole(Role.ARTIST));
        stats.put("totalAdmins", userRepository.countByRole(Role.ADMIN));
        stats.put("pendingArtists", userRepository.countByRoleAndApprovedFalse(Role.ARTIST));
        stats.put("approvedArtists", userRepository.countByRoleAndApprovedTrue(Role.ARTIST));
        stats.put("suspendedUsers", userRepository.countByEnabledFalse());
        stats.put("premiumUsers", userRepository.countByPremiumTrue());
        
        return stats;
    }
    
    // ========== Private Helper Methods ==========
    
    private UserResponse mapToUserResponse(User user) {
        return modelMapper.map(user, UserResponse.class);
    }
}
