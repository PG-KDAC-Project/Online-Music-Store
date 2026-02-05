package com.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Playlist Update Request DTO
 * 
 * Contains data for updating an existing playlist.
 * 
 * @author Music Streaming Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistUpdateRequest {
    
    /**
     * Updated playlist name
     */
    @NotBlank(message = "Playlist name is required")
    @Size(max = 255, message = "Playlist name must not exceed 255 characters")
    private String name;
    
    /**
     * Updated description
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    /**
     * Updated visibility
     */
    private Boolean isPublic;
}
