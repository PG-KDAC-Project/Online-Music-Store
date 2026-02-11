package com.backend.controller;

import com.backend.dto.request.PlaylistCreateRequest;
import com.backend.dto.response.PlaylistResponse;
import com.backend.entity.User;
import com.backend.exception.UnauthorizedException;
import com.backend.repository.UserRepository;
import com.backend.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PlaylistController {
    
    private static final Logger logger = LoggerFactory.getLogger(PlaylistController.class);
    
    private final PlaylistService playlistService;
    private final UserRepository userRepository;
    
    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(
            @Valid @RequestBody PlaylistCreateRequest request,
            Authentication authentication) {
        
        logger.info("Playlist creation request: {}", request.getName());
        
        Long userId = getUserIdFromAuth(authentication);
        PlaylistResponse response = playlistService.createPlaylist(request, userId);
        
        logger.info("Playlist created successfully: ID {}", response.getId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(
            @PathVariable Long id,
            Authentication authentication) {
        
        logger.info("Playlist delete request for ID: {}", id);
        
        Long userId = getUserIdFromAuth(authentication);
        playlistService.deletePlaylist(id, userId);
        
        logger.info("Playlist deleted successfully: ID {}", id);
        
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<PlaylistResponse> addSongToPlaylist(
            @PathVariable Long id,
            @PathVariable Long songId,
            Authentication authentication) {
        
        logger.info("Add song ID: {} to playlist ID: {}", songId, id);
        
        Long userId = getUserIdFromAuth(authentication);
        PlaylistResponse response = playlistService.addSongToPlaylist(id, songId, userId);
        
        logger.info("Song added to playlist successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<PlaylistResponse> removeSongFromPlaylist(
            @PathVariable Long id,
            @PathVariable Long songId,
            Authentication authentication) {
        
        logger.info("Remove song ID: {} from playlist ID: {}", songId, id);
        
        Long userId = getUserIdFromAuth(authentication);
        PlaylistResponse response = playlistService.removeSongFromPlaylist(id, songId, userId);
        
        logger.info("Song removed from playlist successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/songs")
    public ResponseEntity<PlaylistResponse> getPlaylistWithSongs(
            @PathVariable Long id,
            Authentication authentication) {
        
        logger.debug("Get playlist with songs for ID: {}", id);
        
        Long userId = authentication != null ? getUserIdFromAuth(authentication) : null;
        PlaylistResponse response = playlistService.getPlaylistWithSongs(id, userId);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my/all")
    public ResponseEntity<List<PlaylistResponse>> getAllMyPlaylists(
            Authentication authentication) {
        
        logger.debug("Get all my playlists request");
        
        Long userId = getUserIdFromAuth(authentication);
        List<PlaylistResponse> response = playlistService.getUserPlaylists(userId);
        
        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return user.getId();
    }
}