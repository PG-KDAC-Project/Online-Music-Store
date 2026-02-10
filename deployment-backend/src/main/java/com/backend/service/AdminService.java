package com.backend.service;

import com.backend.dto.request.UserStatusUpdateRequest;
import com.backend.dto.response.SongResponse;
import com.backend.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface AdminService {
    
    // ========== User Management ==========
    
 
    UserResponse approveArtist(Long artistId);
    

    UserResponse rejectArtist(Long artistId);
    

    UserResponse suspendUser(Long userId, UserStatusUpdateRequest request);

    UserResponse activateUser(Long userId);
    
    List<UserResponse> getAllUsers();
    
    List<UserResponse> getUsersByRole(String role);
    
    List<UserResponse> getPendingArtists();
    
    Object getUserStatistics();
}
