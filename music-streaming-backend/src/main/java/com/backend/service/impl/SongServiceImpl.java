package com.backend.service.impl;

import com.backend.dto.request.SongUploadRequest;
import com.backend.dto.response.SongResponse;
import com.backend.entity.Song;
import com.backend.entity.User;
import com.backend.exception.FileStorageException;
import com.backend.exception.InvalidFileException;
import com.backend.exception.ResourceNotFoundException;
import com.backend.repository.SongRepository;
import com.backend.repository.UserRepository;
import com.backend.repository.PlaylistRepository;
import com.backend.repository.FavoriteRepository;
import com.backend.service.SongService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {
    
    private static final Logger logger = LoggerFactory.getLogger(SongServiceImpl.class);
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("mp3", "mpeg");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("audio/mpeg", "audio/mp3");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final FavoriteRepository favoriteRepository;
    private final ModelMapper modelMapper;
    
    @Value("${file.upload-dir:uploads/songs}")
    private String uploadDir;
    
    @Value("${file.image-upload-dir:uploads/pictures}")
    private String imageUploadDir;
    
    @Override
    public SongResponse uploadSong(MultipartFile file, SongUploadRequest request, Long artistId) {
        logger.info("Uploading song: {} by artist ID: {}", request.getTitle(), artistId);
        
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", artistId));
        
        validateFile(file);
        String fileName = saveFile(file, artistId);
        
        Song song = Song.builder()
                .title(request.getTitle())
                .artist(artist)
                .album(request.getAlbum())
                .genre(request.getGenre())
                .language(request.getLanguage())
                .duration(request.getDuration())
                .filePath(fileName)
                .playCount(0L)
                .likeCount(0L)
                .build();
        
        Song savedSong = songRepository.save(song);
        logger.info("Song uploaded successfully: ID {}", savedSong.getId());
        
        return mapToResponse(savedSong);
    }
    
    @Override
    public SongResponse updateSong(Long songId, SongUploadRequest request, Long artistId, MultipartFile file) {
        logger.info("Updating song ID: {} by artist ID: {}", songId, artistId);
        
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));
        
        if (!song.getArtist().getId().equals(artistId)) {
            throw new RuntimeException("You are not authorized to update this song");
        }
        
        song.setTitle(request.getTitle());
        song.setAlbum(request.getAlbum());
        song.setGenre(request.getGenre());
        song.setLanguage(request.getLanguage());
        song.setDuration(request.getDuration());
        
        if (file != null && !file.isEmpty()) {
            validateFile(file);
            deleteFile(song.getFilePath());
            String newFileName = saveFile(file, artistId);
            song.setFilePath(newFileName);
        }
        
        Song updatedSong = songRepository.save(song);
        logger.info("Song updated successfully: ID {}", updatedSong.getId());
        
        return mapToResponse(updatedSong);
    }
    
    @Override
    @Transactional
    public void deleteSong(Long songId, Long artistId) {
        logger.info("Deleting song ID: {} by user ID: {}", songId, artistId);
        
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));
        
        if (artistId != null && !song.getArtist().getId().equals(artistId)) {
            throw new RuntimeException("You are not authorized to delete this song");
        }
        
        favoriteRepository.deleteBySongId(songId);
        playlistRepository.removeSongFromAllPlaylists(songId);
        
        deleteFile(song.getFilePath());
        if (song.getCoverImagePath() != null) {
            deleteCoverImage(song.getCoverImagePath());
        }
        
        songRepository.delete(song);
        logger.info("Song deleted successfully: ID {}", songId);
    }
    
    // ========== Retrieval ==========
    
    @Override
    @Transactional(readOnly = true)
    public SongResponse getSongById(Long songId) {
        logger.debug("Fetching song ID: {}", songId);
        
        Song song = songRepository.findByIdWithArtist(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));
        
        return mapToResponse(song);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SongResponse> getAllSongs() {
        logger.debug("Fetching all songs");
        return songRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SongResponse> getSongsByArtist(Long artistId) {
        logger.debug("Fetching songs by artist ID: {}", artistId);
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", "id", artistId));
        return songRepository.findByArtist(artist).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SongResponse> searchSongs(String keyword) {
        logger.debug("Searching songs with keyword: {}", keyword);
        return songRepository.searchSongs(keyword).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SongResponse> getMostPlayedSongs() {
        logger.debug("Fetching most played songs");
        return songRepository.findMostPlayed().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    // ========== Statistics ==========
    
    @Override
    public void incrementPlayCount(Long songId) {
        logger.debug("Incrementing play count for song ID: {}", songId);
        
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));
        
        song.incrementPlayCount();
        songRepository.save(song);
    }
    
    @Override
    public void incrementLikeCount(Long songId) {
        logger.debug("Incrementing like count for song ID: {}", songId);
        
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));
        
        song.incrementLikeCount();
        songRepository.save(song);
    }
    
    @Override
    public void decrementLikeCount(Long songId) {
        logger.debug("Decrementing like count for song ID: {}", songId);
        
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));
        
        song.decrementLikeCount();
        songRepository.save(song);
    }
    
    // ========== File Operations ==========
    
    @Override
    @Transactional(readOnly = true)
    public String getFilePath(Long songId) {
        logger.debug("Getting file path for song ID: {}", songId);
        
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));
        
        Path filePath = Paths.get(uploadDir).resolve(song.getFilePath());
        
        if (!Files.exists(filePath)) {
            throw new FileStorageException("Song file not found: " + song.getFilePath());
        }
        
        return filePath.toAbsolutePath().toString();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getFileSize(Long songId) {
        logger.debug("Getting file size for song ID: {}", songId);
        
        String filePath = getFilePath(songId);
        
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException ex) {
            throw new FileStorageException("Failed to get file size", ex);
        }
    }
    
    @Override
    public void uploadCoverImage(Long songId, MultipartFile coverImage) {
        logger.info("Uploading cover image for song ID: {}", songId);
        
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song", "id", songId));
        
        String imagePath = saveCoverImage(coverImage, songId);
        song.setCoverImagePath(imagePath);
        songRepository.save(song);
        
        logger.info("Cover image uploaded successfully for song ID: {}", songId);
    }
    
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File is empty. Please upload a valid MP3 file.");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                String.format("File size exceeds maximum limit of %d MB", MAX_FILE_SIZE / (1024 * 1024))
            );
        }
        
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new InvalidFileException("Invalid file path: " + originalFilename);
        }
        
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new InvalidFileException(
                "Invalid file type. Only MP3 files are allowed. Uploaded: " + extension
            );
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException(
                "Invalid MIME type. Expected audio/mpeg, but got: " + contentType
            );
        }
        
        logger.info("File validation passed: {} (size: {} bytes, type: {})", 
            originalFilename, file.getSize(), contentType);
    }
    
    private String saveFile(MultipartFile file, Long artistId) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created upload directory: {}", uploadPath.toAbsolutePath());
            }
            
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = getFileExtension(originalFilename);
            String uniqueFilename = String.format("%d_%s.%s", 
                artistId, 
                UUID.randomUUID().toString(), 
                extension
            );
            
            Path filePath = uploadPath.resolve(uniqueFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            logger.info("File saved successfully: {}", filePath.toAbsolutePath());
            return uniqueFilename;
            
        } catch (IOException ex) {
            throw new FileStorageException("Failed to store file", ex);
        }
    }
    
    private void deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
            logger.info("File deleted: {}", filePath.toAbsolutePath());
        } catch (IOException ex) {
            logger.error("Failed to delete file: {}", fileName, ex);
        }
    }
    
    private void deleteCoverImage(String fileName) {
        try {
            Path filePath = Paths.get(imageUploadDir).resolve(fileName);
            Files.deleteIfExists(filePath);
            logger.info("Cover image deleted: {}", filePath.toAbsolutePath());
        } catch (IOException ex) {
            logger.error("Failed to delete cover image: {}", fileName, ex);
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
    
    private SongResponse mapToResponse(Song song) {
        SongResponse response = modelMapper.map(song, SongResponse.class);
        response.setArtistName(song.getArtist().getName());
        response.setArtistId(song.getArtist().getId());
        response.setFormattedDuration(song.getFormattedDuration());
        return response;
    }
    
    private String saveCoverImage(MultipartFile file, Long songId) {
        try {
            Path uploadPath = Paths.get(imageUploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            String extension = getFileExtension(file.getOriginalFilename());
            String uniqueFilename = String.format("song_%d_%s.%s", 
                songId, UUID.randomUUID().toString(), extension);
            
            Path filePath = uploadPath.resolve(uniqueFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            
            return uniqueFilename;
        } catch (IOException ex) {
            throw new FileStorageException("Failed to store cover image", ex);
        }
    }
}
