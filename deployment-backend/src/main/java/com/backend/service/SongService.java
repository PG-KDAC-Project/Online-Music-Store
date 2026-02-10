package com.backend.service;

import com.backend.dto.request.SongUploadRequest;
import com.backend.dto.response.SongResponse;
import com.backend.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface SongService {
    
    // ========== Upload & Management ==========
    

    SongResponse uploadSong(MultipartFile file, SongUploadRequest request, Long artistId);

    SongResponse updateSong(Long songId, SongUploadRequest request, Long artistId, MultipartFile file);
    

    void deleteSong(Long songId, Long artistId);
    
    // ========== Retrieval ==========
    
  
    SongResponse getSongById(Long songId);
    
 
    List<SongResponse> getAllSongs();
    
    List<SongResponse> getSongsByArtist(Long artistId);
    
    List<SongResponse> searchSongs(String keyword);
    
    List<SongResponse> getMostPlayedSongs();
    
    // ========== Statistics ==========

    void incrementPlayCount(Long songId);
    

    void incrementLikeCount(Long songId);
    
 
    void decrementLikeCount(Long songId);
    
    // ========== File Operations ==========

    String getFilePath(Long songId);
    

    long getFileSize(Long songId);
    

    void uploadCoverImage(Long songId, MultipartFile coverImage);
}
