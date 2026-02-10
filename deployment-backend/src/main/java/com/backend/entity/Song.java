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
    private String artist;
    private String album;
    private String genre;
    private String language; // Restored
    
    private Double duration; 
    
    // Paths
    private String songUrl;
    private String thumbnailUrl;
    private String filePath;       // Restored
    private String coverImagePath; // Restored

    // Counters
    @Builder.Default
    private Long playCount = 0L;   // Restored
    
    @Builder.Default
    private Long likeCount = 0L;   // Restored

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Restored

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (playCount == null) playCount = 0L;
        if (likeCount == null) likeCount = 0L;
    }

    // --- DATABASE FIX: mappedBy ---
    @ManyToMany(mappedBy = "songs", fetch = FetchType.LAZY)
    private List<Playlist> playlists = new ArrayList<>();

    // --- RESTORED BUSINESS LOGIC ---

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
