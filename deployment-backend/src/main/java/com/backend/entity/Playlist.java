package com.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    
    @Column(name = "is_public")
    private Boolean isPublic; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // --- DATABASE FIX: @JoinTable to create the table ---
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "playlist_songs",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs = new ArrayList<>();

    // --- RESTORED LOGIC ---

    // Must return boolean to satisfy PlaylistServiceImpl
    public boolean addSong(Song song) {
        if (this.songs == null) {
            this.songs = new ArrayList<>();
        }
        this.songs.add(song);
        return true; 
    }

    public void removeSongById(Long songId) {
        if (this.songs!= null) {
            this.songs.removeIf(song -> song.getId().equals(songId));
        }
    }

    public boolean isOwnedBy(Long userId) {
        return this.user!= null && this.user.getId().equals(userId);
    }

    public Integer getSongCount() {
        return this.songs!= null? this.songs.size() : 0;
    }

    // Returns Integer to satisfy ServiceImpl expectation
    public Integer getTotalDuration() {
        if (this.songs == null) return 0;
        return (int) this.songs.stream()
              .mapToDouble(song -> song.getDuration()!= null? song.getDuration() : 0.0)
              .sum();
    }

    public String getFormattedTotalDuration() {
        Integer totalSeconds = getTotalDuration();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    public Boolean getIsPublic() {
        return isPublic;
    }
}
