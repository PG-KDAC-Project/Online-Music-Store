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
    
    // Added based on logs
    @Column(name = "is_public")
    private Boolean isPublic; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // --- DATABASE FIX: @JoinTable ---
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "playlist_songs",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    private List<Song> songs = new ArrayList<>();

    // --- RESTORED BUSINESS LOGIC ---

    public void addSong(Song song) {
        if (this.songs == null) {
            this.songs = new ArrayList<>();
        }
        this.songs.add(song);
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

    public Double getTotalDuration() {
        if (this.songs == null) return 0.0;
        return this.songs.stream()
               .mapToDouble(Song::getDuration)
               .sum();
    }

    public String getFormattedTotalDuration() {
        Double totalSeconds = getTotalDuration();
        long minutes = (long) (totalSeconds / 60);
        long seconds = (long) (totalSeconds % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
    
    // Getter manually added for Lombok consistency if needed
    public Boolean getIsPublic() {
        return isPublic;
    }
}
