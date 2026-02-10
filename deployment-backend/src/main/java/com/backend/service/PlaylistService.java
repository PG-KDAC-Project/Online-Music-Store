package com.backend.service;

import com.backend.dto.request.PlaylistCreateRequest;
import com.backend.dto.request.PlaylistUpdateRequest;
import com.backend.dto.response.PlaylistResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Playlist Service Interface
 * 
 * Handles playlist-related operations including:
 * - Playlist creation and management
 * - Song operations (add/remove)
 * - User playlists retrieval
 * - Public playlist discovery
 * 
 * @author Music Streaming Team
 * @since 1.0
 */
public interface PlaylistService {
    
    // ========== Playlist Management ==========
    
    /**
     * Create a new playlist
     * 
     * @param request playlist details
     * @param userId user ID (owner)
     * @return created playlist response
     * @throws ResourceNotFoundException if user not found
     */
    PlaylistResponse createPlaylist(PlaylistCreateRequest request, Long userId);
    
    void deletePlaylist(Long playlistId, Long userId);
    
    // ========== Song Operations ==========
    
    /**
     * Add a song to playlist
     * 
     * @param playlistId playlist ID
     * @param songId song ID to add
     * @param userId user ID (for authorization)
     * @return updated playlist response
     * @throws ResourceNotFoundException if playlist or song not found
     * @throws UnauthorizedException if user doesn't own the playlist
     */
    PlaylistResponse addSongToPlaylist(Long playlistId, Long songId, Long userId);
    
    /**
     * Remove a song from playlist
     * 
     * @param playlistId playlist ID
     * @param songId song ID to remove
     * @param userId user ID (for authorization)
     * @return updated playlist response
     * @throws ResourceNotFoundException if playlist not found
     * @throws UnauthorizedException if user doesn't own the playlist
     */
    PlaylistResponse removeSongFromPlaylist(Long playlistId, Long songId, Long userId);
    
    // ========== Retrieval ==========
    
    /**
     * Get playlist by ID with songs
     * 
     * @param playlistId playlist ID
     * @param userId current user ID (for authorization check)
     * @return playlist response with songs
     * @throws ResourceNotFoundException if playlist not found
     * @throws UnauthorizedException if playlist is private and user doesn't own it
     */
    PlaylistResponse getPlaylistWithSongs(Long playlistId, Long userId);
    
    List<PlaylistResponse> getUserPlaylists(Long userId);
}
