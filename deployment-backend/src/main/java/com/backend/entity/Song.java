package com.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // --- CRITICAL FIX: Artist must be a User entity, not a String ---
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "artist_id")
    private User artist;

    private String album;
    private String genre;
    private String language;
    
    // Duration as Double to match Service expectations
    private Double duration; 
    
    private String songUrl;
    private String thumbnailUrl;
    private String filePath;
    private String coverImagePath;

    @Builder.Default
    private Long playCount = 0L;
    
    @Builder.Default
    private Long likeCount = 0L;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (playCount == null) playCount = 0L;
        if (likeCount == null) likeCount = 0L;
    }

    // --- DATABASE FIX: mappedBy for Playlist ---
    @ManyToMany(mappedBy = "songs", fetch = FetchType.LAZY)
    private List<Playlist> playlists = new ArrayList<>();

    // --- RESTORED HELPER METHODS FOR SERVICE LAYER ---

    public String getFormattedDuration() {
        if (duration == null) return "00:00";
        long minutes = (long) (duration / 60);
        long seconds = (long) (duration % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void incrementPlayCount() {
        if (this.playCount == null) this.playCount = 0L;
        this.playCount++;
    }

    public void incrementLikeCount() {
        if (this.likeCount == null) this.likeCount = 0L;
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount!= null && this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
