package com.backend.dto.response;

import com.backend.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Boolean enabled;
    private Boolean approved;
    private Boolean premium;
    private LocalDateTime createdAt;
}
