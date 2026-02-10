package com.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "songs", indexes = {
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_genre", columnList = "genre"),
    @Index(name = "idx_artist_id", columnList = "artist_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Song {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;
    
    @Column(length = 255)
    private String album;
    
    @Column(length = 50)
    private String genre;
    
    @Column(length = 50)
    private String language;
    
    @Column(nullable = false)
    private Integer duration;
    
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    
    @Column(name = "cover_image_path", length = 500)
    private String coverImagePath;
    
    @Column(name = "play_count", nullable = false)
    @Builder.Default
    private Long playCount = 0L;
    
    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Long likeCount = 0L;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void incrementPlayCount() {
        this.playCount++;
    }
    
    public void incrementLikeCount() {
        this.likeCount++;
    }
    
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
    
    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}
