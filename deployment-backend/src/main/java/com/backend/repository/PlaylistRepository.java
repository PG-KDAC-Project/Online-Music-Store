package com.backend.repository;

import com.backend.entity.Playlist;
import com.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    
    @Query("SELECT p FROM Playlist p LEFT JOIN FETCH p.songs WHERE p.id = :id")
    Optional<Playlist> findByIdWithSongs(@Param("id") Long id);
    
    @Query("SELECT p FROM Playlist p JOIN FETCH p.user LEFT JOIN FETCH p.songs WHERE p.id = :id")
    Optional<Playlist> findByIdWithUserAndSongs(@Param("id") Long id);
    
    List<Playlist> findByUserId(Long userId);
    
    @Modifying
    @Query(value = "DELETE FROM playlist_songs WHERE song_id = :songId", nativeQuery = true)
    void removeSongFromAllPlaylists(@Param("songId") Long songId);
}
