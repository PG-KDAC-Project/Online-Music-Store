package com.backend.repository;

import com.backend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    Optional<Favorite> findByUserIdAndSongId(Long userId, Long songId);
    
    @Query("SELECT f FROM Favorite f JOIN FETCH f.song s JOIN FETCH s.artist WHERE f.user.id = :userId")
    List<Favorite> findByUserId(Long userId);
    
    boolean existsByUserIdAndSongId(Long userId, Long songId);
    
    void deleteByUserIdAndSongId(Long userId, Long songId);
    
    @Modifying
    void deleteBySongId(Long songId);
}
