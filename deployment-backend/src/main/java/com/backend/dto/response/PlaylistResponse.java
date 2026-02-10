package com.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Playlist Response DTO
 * 
 * Contains playlist information returned to clients.
 * 
 * @author Music Streaming Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistResponse {
    
    private Long id;
    private String name;
    private String description;
    private Long userId;
    private String userName;
    private Boolean isPublic;
    private Integer songCount;
    private Integer totalDuration;
    private String formattedDuration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SongResponse> songs;
}
