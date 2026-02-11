package com.backend.controller;

import com.backend.dto.response.SongResponse;
import com.backend.entity.Favorite;
import com.backend.entity.Song;
import com.backend.entity.User;
import com.backend.repository.FavoriteRepository;
import com.backend.repository.SongRepository;
import com.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
//@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FavoriteController {
    
    private final FavoriteRepository favoriteRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    
    @PostMapping("/songs/{songId}")
    public ResponseEntity<Void> likeSong(@PathVariable Long songId, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        
        if (!favoriteRepository.existsByUserIdAndSongId(userId, songId)) {
            User user = userRepository.findById(userId).orElseThrow();
            Song song = songRepository.findById(songId).orElseThrow();
            
            Favorite favorite = Favorite.builder()
                .user(user)
                .song(song)
                .build();
            
            favoriteRepository.save(favorite);
            song.incrementLikeCount();
            songRepository.save(song);
        }
        
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/songs/{songId}")
    @Transactional
    public ResponseEntity<Void> unlikeSong(@PathVariable Long songId, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        
        if (favoriteRepository.existsByUserIdAndSongId(userId, songId)) {
            favoriteRepository.deleteByUserIdAndSongId(userId, songId);
            
            Song song = songRepository.findById(songId).orElseThrow();
            song.decrementLikeCount();
            songRepository.save(song);
        }
        
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/songs")
    public ResponseEntity<List<SongResponse>> getFavoriteSongs(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        
        List<SongResponse> songs = favorites.stream().map(fav -> {
            Song song = fav.getSong();
            return SongResponse.builder()
                .id(song.getId())
                .title(song.getTitle())
                .artistId(song.getArtist().getId())
                .artistName(song.getArtist().getName())
                .album(song.getAlbum())
                .genre(song.getGenre())
                .language(song.getLanguage())
                .duration(song.getDuration())
                .formattedDuration(song.getFormattedDuration())
                .filePath(song.getFilePath())
                .coverImagePath(song.getCoverImagePath())
                .playCount(song.getPlayCount())
                .likeCount(song.getLikeCount())
                .createdAt(song.getCreatedAt())
                .build();
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(songs);
    }
    
    @GetMapping("/songs/{songId}/check")
    public ResponseEntity<Boolean> checkIfLiked(@PathVariable Long songId, Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        boolean isLiked = favoriteRepository.existsByUserIdAndSongId(userId, songId);
        return ResponseEntity.ok(isLiked);
    }
    
    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getId();
    }
}
