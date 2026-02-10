package com.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Playlist Create Request DTO
 * 
 * Contains data for creating a new playlist.
 * 
 * @author Music Streaming Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistCreateRequest {
    
    /**
     * Playlist name
     */
    @NotBlank(message = "Playlist name is required")
    @Size(max = 255, message = "Playlist name must not exceed 255 characters")
    private String name;
    
    /**
     * Playlist description (optional)
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    /**
     * Public or private playlist
     */
    private Boolean isPublic;
}
