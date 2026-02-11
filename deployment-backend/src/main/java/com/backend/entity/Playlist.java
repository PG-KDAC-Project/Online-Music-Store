package com.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Playlist Entity
 * 
 * Represents a user-created playlist containing multiple songs.
 * Users can create, manage, and share playlists with curated collections of songs.
 * 
 * @author Music Streaming Team
 * @since 1.0
 */
@Entity
@Table(name = "playlists", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "songs"})
@EqualsAndHashCode(of = "id")
public class Playlist {
    
    /**
     * Unique identifier for the playlist
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Playlist name
     */
    @Column(nullable = false, length = 255)
    private String name;
    
    /**
     * User who owns the playlist
     * Many playlists can belong to one user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Songs in the playlist
     * Many-to-many relationship - a playlist can have many songs,
     * and a song can be in many playlists
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "playlist_songs",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id"),
        indexes = {
            @Index(name = "idx_playlist_id", columnList = "playlist_id"),
            @Index(name = "idx_song_id", columnList = "song_id")
        }
    )
    @Builder.Default
    private List<Song> songs = new ArrayList<>();
    
    /**
     * Playlist description (optional)
     */
    @Column(length = 500)
    private String description;
    
    /**
     * Playlist visibility
     * Public playlists can be viewed by anyone
     * Private playlists are only visible to the owner
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPublic = false;
    
    /**
     * Playlist creation timestamp
     * Automatically set when entity is persisted
     */
    @Column(nullable = false, updatable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    /**
     * Last updated timestamp
     * Automatically updated when entity is modified
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Pre-persist lifecycle callback
     * Initializes default values before entity is saved
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
        
        if (isPublic == null) {
            isPublic = false;
        }
    }
    
    /**
     * Pre-update lifecycle callback
     * Updates the updatedAt timestamp when entity is modified
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // ========== Business Methods ==========
    
    /**
     * Add a song to the playlist
     * 
     * @param song song to add
     * @return true if song was added, false if already exists
     */
    public boolean addSong(Song song) {
        if (song == null) {
            return false;
        }
        
        if (!songs.contains(song)) {
            songs.add(song);
            return true;
        }
        
        return false;
    }
    
    /**
     * Remove a song from the playlist
     * 
     * @param song song to remove
     * @return true if song was removed, false if not found
     */
    public boolean removeSong(Song song) {
        return songs.remove(song);
    }
    
    /**
     * Remove a song by ID from the playlist
     * 
     * @param songId song ID to remove
     * @return true if song was removed, false if not found
     */
    public boolean removeSongById(Long songId) {
        return songs.removeIf(song -> song.getId().equals(songId));
    }
    
    /**
     * Check if playlist contains a specific song
     * 
     * @param song song to check
     * @return true if playlist contains the song
     */
    public boolean containsSong(Song song) {
        return songs.contains(song);
    }
    
    /**
     * Check if playlist contains a song by ID
     * 
     * @param songId song ID to check
     * @return true if playlist contains the song
     */
    public boolean containsSongById(Long songId) {
        return songs.stream().anyMatch(song -> song.getId().equals(songId));
    }
    
    /**
     * Get the number of songs in the playlist
     * 
     * @return number of songs
     */
    public int getSongCount() {
        return songs != null ? songs.size() : 0;
    }
    
    /**
     * Calculate total duration of all songs in the playlist
     * 
     * @return total duration in seconds
     */
    public int getTotalDuration() {
        if (songs == null || songs.isEmpty()) {
            return 0;
        }
        
        return songs.stream()
                    .mapToInt(Song::getDuration)
                    .sum();
    }
    
    /**
     * Get formatted total duration (HH:MM:SS)
     * 
     * @return formatted duration string
     */
    public String getFormattedTotalDuration() {
        int totalSeconds = getTotalDuration();
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    /**
     * Clear all songs from the playlist
     */
    public void clearSongs() {
        if (songs != null) {
            songs.clear();
        }
    }
    
    /**
     * Check if playlist is empty
     * 
     * @return true if playlist has no songs
     */
    public boolean isEmpty() {
        return songs == null || songs.isEmpty();
    }
    
    /**
     * Check if user owns this playlist
     * 
     * @param userId user ID to check
     * @return true if user owns the playlist
     */
    public boolean isOwnedBy(Long userId) {
        return user != null && user.getId().equals(userId);
    }
}
