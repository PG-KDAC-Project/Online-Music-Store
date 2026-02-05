package com.backend.repository;

import com.backend.entity.Song;
import com.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    
    @Query("SELECT s FROM Song s JOIN FETCH s.artist WHERE s.id = :id")
    Optional<Song> findByIdWithArtist(@Param("id") Long id);
    
    List<Song> findByArtist(User artist);
    
    @Query("SELECT s FROM Song s WHERE " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.album) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.genre) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Song> searchSongs(@Param("keyword") String keyword);
    
    @Query("SELECT s FROM Song s ORDER BY s.playCount DESC")
    List<Song> findMostPlayed();
}
