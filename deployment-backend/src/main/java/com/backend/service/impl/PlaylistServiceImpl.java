package com.backend.service.impl;

import com.backend.dto.request.PlaylistCreateRequest;
import com.backend.dto.response.PlaylistResponse;
import com.backend.dto.response.SongResponse;
import com.backend.entity.Playlist;
import com.backend.entity.Song;
import com.backend.entity.User;
import com.backend.exception.ResourceNotFoundException;
import com.backend.exception.UnauthorizedException;
import com.backend.repository.PlaylistRepository;
import com.backend.repository.SongRepository;
import com.backend.repository.UserRepository;
import com.backend.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
    
    private static final Logger logger = LoggerFactory.getLogger(PlaylistServiceImpl.class);
    
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ModelMapper modelMapper;
    
    // ========== Playlist Management ==========
    
    @Override
    public PlaylistResponse createPlaylist(PlaylistCreateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Playlist playlist = Playlist.builder()
                .name(request.getName())
                .description(request.getDescription())
                .user(user)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .build();
        
        Playlist savedPlaylist = playlistRepository.save(playlist);
        logger.info("Playlist created: ID {}", savedPlaylist.getId());
        
        return mapToResponse(savedPlaylist, false);
    }
    
    @Override
    public void deletePlaylist(Long playlistId, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist", "id", playlistId));
        
        verifyOwnership(playlist, userId);
        playlistRepository.delete(playlist);
        logger.info("Playlist deleted: ID {}", playlistId);
    }
    
    // ========== Song Operations ==========
    
    @Override
    public PlaylistResponse addSongToPlaylist(Long playlistId, Long songId, Long userId) {
        Playlist playlist = playlistRepository.findByIdWithSongs(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist", "id", playlistId));
        
        verifyOwnership(playlist, userId);
        
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));
        
        boolean added = playlist.addSong(song);
        
        if (added) {
            playlistRepository.save(playlist);
        }
        
        return mapToResponse(playlist, true);
    }
    
    @Override
    public PlaylistResponse removeSongFromPlaylist(Long playlistId, Long songId, Long userId) {
        Playlist playlist = playlistRepository.findByIdWithSongs(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist", "id", playlistId));
        
        verifyOwnership(playlist, userId);
        
        boolean removed = playlist.removeSongById(songId);
        
        if (removed) {
            playlistRepository.save(playlist);
        }
        
        return mapToResponse(playlist, true);
    }
    
    // ========== Retrieval ==========
    
    @Override
    @Transactional(readOnly = true)
    public PlaylistResponse getPlaylistWithSongs(Long playlistId, Long userId) {
        Playlist playlist = playlistRepository.findByIdWithUserAndSongs(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist", "id", playlistId));
        
        checkVisibility(playlist, userId);
        
        return mapToResponse(playlist, true);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PlaylistResponse> getUserPlaylists(Long userId) {
        return playlistRepository.findByUserId(userId).stream()
                .map(playlist -> mapToResponse(playlist, false))
                .collect(Collectors.toList());
    }
    
    // ========== Private Helper Methods ==========
    
    private void verifyOwnership(Playlist playlist, Long userId) {
        if (!playlist.isOwnedBy(userId)) {
            throw new UnauthorizedException("You don't have permission to modify this playlist");
        }
    }
    
    private void checkVisibility(Playlist playlist, Long userId) {
        if (!playlist.getIsPublic() && (userId == null || !playlist.isOwnedBy(userId))) {
            throw new UnauthorizedException("This playlist is private");
        }
    }
    
    private PlaylistResponse mapToResponse(Playlist playlist, boolean includeSongs) {
        PlaylistResponse response = modelMapper.map(playlist, PlaylistResponse.class);
        response.setUserId(playlist.getUser().getId());
        response.setUserName(playlist.getUser().getName());
        response.setSongCount(playlist.getSongCount());
        response.setTotalDuration(playlist.getTotalDuration());
        response.setFormattedDuration(playlist.getFormattedTotalDuration());
        
        if (includeSongs && playlist.getSongs() != null) {
            List<SongResponse> songResponses = playlist.getSongs().stream()
                    .map(this::mapSongToResponse)
                    .collect(Collectors.toList());
            response.setSongs(songResponses);
        }
        
        return response;
    }
    
    private SongResponse mapSongToResponse(Song song) {
        SongResponse response = modelMapper.map(song, SongResponse.class);
        response.setArtistName(song.getArtist().getName());
        response.setArtistId(song.getArtist().getId());
        response.setFormattedDuration(song.getFormattedDuration());
        return response;
    }
}
