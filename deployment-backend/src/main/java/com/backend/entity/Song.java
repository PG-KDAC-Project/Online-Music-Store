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
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;
    private String album;
    private String genre;
    private Double duration;
    private String songUrl;
    private String thumbnailUrl;

    // mappedBy tells Hibernate that Playlist owns the relationship
    @ManyToMany(mappedBy = "songs", fetch = FetchType.LAZY)
    private List<Playlist> playlists = new ArrayList<>();
}
