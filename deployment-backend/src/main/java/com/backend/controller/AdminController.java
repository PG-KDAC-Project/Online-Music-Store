package com.backend.controller;

import com.backend.dto.request.PremiumPackageRequest;
import com.backend.dto.request.UserStatusUpdateRequest;
import com.backend.dto.response.PremiumPackageResponse;
import com.backend.dto.response.UserResponse;
import com.backend.service.AdminService;
import com.backend.service.PremiumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


 
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final AdminService adminService;
    private final PremiumService premiumService;
    
    
    /**
     * Approve an artist (ADMIN role)
     * 
     * Allows admins to approve artists, enabling them to upload content.
     * artistId artist ID to approve
     *  ResponseEntity with updated user details
     */
    @PostMapping("/artists/{artistId}/approve")
    public ResponseEntity<UserResponse> approveArtist(@PathVariable Long artistId) {
        logger.info("Admin: Approve artist request for ID: {}", artistId);
        
        UserResponse response = adminService.approveArtist(artistId);
        
        logger.info("Artist approved successfully: ID {}", artistId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reject an artist (ADMIN role)
     * 
     * Sets artist's approved status to false.
     * artistId artist ID to reject
     *  ResponseEntity with updated user details
     */
    @PostMapping("/artists/{artistId}/reject")
    public ResponseEntity<UserResponse> rejectArtist(@PathVariable Long artistId) {
        logger.info("Admin: Reject artist request for ID: {}", artistId);
        
        UserResponse response = adminService.rejectArtist(artistId);
        
        logger.info("Artist rejected: ID {}", artistId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get pending artists (awaiting approval)
     *  ResponseEntity with list of pending artists
     */
    @GetMapping("/artists/pending")
    public ResponseEntity<List<UserResponse>> getPendingArtists() {
        logger.debug("Admin: Get pending artists request");
        List<UserResponse> response = adminService.getPendingArtists();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Suspend a user (ADMIN role)
     * Disables user account, preventing login and access.
     *  userId user ID to suspend
     *  request suspension details with reason
     *  ResponseEntity with updated user details
     */
    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<UserResponse> suspendUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        
        logger.info("Admin: Suspend user request for ID: {}. Reason: {}", userId, request.getReason());
        
        UserResponse response = adminService.suspendUser(userId, request);
        
        logger.info("User suspended successfully: ID {}", userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Activate a user (re-enable after suspension)
     * 
     * userId user ID to activate
     *  ResponseEntity with updated user details
     */
    @PostMapping("/users/{userId}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long userId) {
        logger.info("Admin: Activate user request for ID: {}", userId);
        UserResponse response = adminService.activateUser(userId);
        logger.info("User activated successfully: ID {}", userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * View all users (ADMIN role)
     * Returns all users in the system.
     *  ResponseEntity with  list of users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        logger.debug("Admin: Get all users request");
        List<UserResponse> response = adminService.getAllUsers();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get users by role
     *  role user role (ADMIN, ARTIST, LISTENER)
     *  ResponseEntity with  list of users
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        logger.debug("Admin: Get users by role: {}", role);
        List<UserResponse> response = adminService.getUsersByRole(role);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get user statistics
     * 
     * Returns summary statistics about users (total, by role, pending, etc.)
     * 
     *  ResponseEntity with user statistics
     */
    @GetMapping("/statistics/users")
    public ResponseEntity<Object> getUserStatistics() {
        logger.debug("Admin: Get user statistics request");
        
        Object response = adminService.getUserStatistics();
        return ResponseEntity.ok(response);
    }
        
    @PostMapping("/premium/package")
    public ResponseEntity<PremiumPackageResponse> setPremiumPackage(
            @Valid @RequestBody PremiumPackageRequest request) {
        
        logger.info("Admin: Set premium package request");
        PremiumPackageResponse response = premiumService.setPremiumPackage(request);
        logger.info("Premium package set successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/premium/package")
    public ResponseEntity<PremiumPackageResponse> getPremiumPackage() {
        logger.debug("Admin: Get premium package request");
        PremiumPackageResponse response = premiumService.getPremiumPackage();
        return ResponseEntity.ok(response);
    }
}
