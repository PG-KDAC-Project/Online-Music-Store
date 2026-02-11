package com.backend.controller;

import com.backend.dto.request.SongUploadRequest;
import com.backend.dto.response.SongResponse;
import com.backend.entity.User;
import com.backend.exception.UnauthorizedException;
import com.backend.repository.UserRepository;
import com.backend.service.SongService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class SongController {
    
    private static final Logger logger = LoggerFactory.getLogger(SongController.class);
    private static final int CHUNK_SIZE = 1024 * 256;
    
    private final SongService songService;
    private final UserRepository userRepository;
    
    // ========== Upload & Management ==========
    
    /**
     *file MP3 audio file
     * ResponseEntity with uploaded song details
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<SongResponse> uploadSong(
            @RequestParam("file") MultipartFile file,@RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam("title") String title,@RequestParam(value = "album", required = false) String album,
            @RequestParam(value = "genre", required = false) String genre,@RequestParam(value = "language", required = false) String language,
            @RequestParam("duration") Integer duration,
            Authentication authentication) {
        
        // Create request object from individual parameters
        SongUploadRequest request = SongUploadRequest.builder()
                .title(title)
                .album(album)
                .genre(genre)
                .language(language)
                .duration(duration)
                .build();
        
        logger.info("Song upload request received: {}", request.getTitle());
        Long artistId = getUserIdFromAuth(authentication);
        // Verify artist is approved
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new UnauthorizedException("Artist not found"));
        if (!artist.canUploadContent()) {
            throw new UnauthorizedException("Your artist account is not approved for uploading content");
        }
        SongResponse response = songService.uploadSong(file, request, artistId);
        // Upload cover image if provided
        if (coverImage != null && !coverImage.isEmpty()) {
            songService.uploadCoverImage(response.getId(), coverImage);
        }
        logger.info("Song uploaded successfully: ID {}, title: {}", 
                    response.getId(), response.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    /**
     * Update song with optional file and cover image (ARTIST only - owner)
     * ResponseEntity with updated song details
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<SongResponse> updateSong(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,@RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam("title") String title,@RequestParam(value = "album", required = false) String album,
            @RequestParam(value = "genre", required = false) String genre,@RequestParam(value = "language", required = false) String language,
            @RequestParam("duration") Integer duration,
            Authentication authentication) {
        
        logger.info("Song update request for ID: {}", id);
        
        SongUploadRequest request = SongUploadRequest.builder()
                .title(title)
                .album(album)
                .genre(genre)
                .language(language)
                .duration(duration)
                .build();
        
        Long artistId = getUserIdFromAuth(authentication);
        SongResponse response = songService.updateSong(id, request, artistId, file);
        if (coverImage != null && !coverImage.isEmpty()) {
            songService.uploadCoverImage(id, coverImage);
        }
        logger.info("Song updated successfully: ID {}", id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete a song (ARTIST only - owner, or ADMIN)
     * 
     *  id song ID
     * authentication current user authentication
     * ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ARTIST', 'ADMIN')")
    public ResponseEntity<Void> deleteSong(
            @PathVariable Long id,
            Authentication authentication) {
        logger.info("Song delete request for ID: {}", id);
        
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        if (user.isAdmin()) {
            songService.deleteSong(id, null);
        } else {	
            songService.deleteSong(id, user.getId());
        }
        logger.info("Song deleted successfully: ID {}", id);
        
        return ResponseEntity.noContent().build();
    }
        /**
     * Stream song with byte-range support
     * 
     * Supports HTTP Range requests for efficient streaming.
     * Allows seeking and partial content delivery.
     * no authentication required.
     * 
     * id song ID
     * request HTTP servlet request (for Range header)
     * response HTTP servlet response
     * 
     */
    @GetMapping("/stream/{id}")
    public void streamSong(
            @PathVariable Long id,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        
        logger.info("Stream request for song ID: {}", id);
        
        String filePath = songService.getFilePath(id);
        File file = new File(filePath);
        long fileSize = file.length();
        songService.incrementPlayCount(id);
        // Get Range header
        String rangeHeader = request.getHeader(HttpHeaders.RANGE);
        
        if (rangeHeader == null) {
            // No range - send full file
            streamFullFile(file, response);
        } else {
            // Parse range and send partial content
            streamPartialFile(file, fileSize, rangeHeader, response);
        }
    }
    
    /**
     * Download song (PREMIUM users only)
     * Allows premium users to download songs for offline playback.
     * Requires authentication and premium subscription.
     *  authentication current user authentication
     *  ResponseEntity with file resource
     */
    @GetMapping("/download/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadSong(
            @PathVariable Long id,
            Authentication authentication) {
        
        logger.info("Download request for song ID: {}", id);
        
        Long userId = getUserIdFromAuth(authentication);
        
        // Verify premium status
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        if (!user.isPremium() && !user.isAdmin()) {
            throw new UnauthorizedException("Premium subscription required to download songs");
        }
        
        // Get file
        String filePath = songService.getFilePath(id);
        Resource resource = new FileSystemResource(filePath);
        
        // Get song details for filename
        SongResponse song = songService.getSongById(id);
        String filename = sanitizeFilename(song.getTitle() + " - " + song.getArtistName() + ".mp3");
        
        logger.info("Song download initiated: {} by user ID: {}", filename, userId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
    /**
     * Get song by ID
     * id song ID
     *  ResponseEntity with song details
     */
    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSongById(@PathVariable Long id) {
        logger.debug("Get song request for ID: {}", id);
        return ResponseEntity.ok(songService.getSongById(id));
    }
    
    /**
     * Get all songs
     *  ResponseEntity  of songs
     */
    @GetMapping
    public ResponseEntity<List<SongResponse>> getAllSongs() {
        logger.debug("Get all songs request");
        return ResponseEntity.ok(songService.getAllSongs());
    }
    
    /**
     * Get songs for current logged-in artist
     * authentication current user authentication
     *  ResponseEntity with  songs
     */
    @GetMapping("/my-songs")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<List<SongResponse>> getMySongs(Authentication authentication) {
        Long artistId = getUserIdFromAuth(authentication);
        logger.debug("Get my songs for artist ID: {}", artistId);
        return ResponseEntity.ok(songService.getSongsByArtist(artistId));
    }
    
    /**
     * Search songs by keyword
     *  keyword search term
     * ResponseEntity with  matching songs
     */
    @GetMapping("/search")
    public ResponseEntity<List<SongResponse>> searchSongs(@RequestParam String keyword) {
        logger.debug("Search songs with keyword: {}", keyword);
        return ResponseEntity.ok(songService.searchSongs(keyword));
    }    
    /**
     * Get most played songs
     *  ResponseEntity with most played songs
     */
    @GetMapping("/most-played")
    public ResponseEntity<List<SongResponse>> getMostPlayed() {
        logger.debug("Get most played songs");
        return ResponseEntity.ok(songService.getMostPlayedSongs());
    }
    //  Helper Methods ***
    
    /**
     * Extract user ID from authentication
     *  authentication Spring Security authentication
     *  user ID
     */
    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return user.getId();
    }
    
    /**
     * Stream full file without range
     *  file file to stream
     * response HTTP response
     * IOException if streaming fails
     */
    private void streamFullFile(File file, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("audio/mpeg");
        response.setContentLengthLong(file.length());
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        
        Files.copy(file.toPath(), response.getOutputStream());
        response.getOutputStream().flush();
        
        logger.debug("Streamed full file: {} ({} bytes)", file.getName(), file.length());
    }
    
    /**
     * Stream partial file with byte-range support
     * Supports HTTP Range requests for seeking and partial content delivery.
     * file file to stream
     * fileSize total file size
     * rangeHeader Range header value
     * response HTTP response
     */
    private void streamPartialFile(File file, long fileSize, String rangeHeader, 
                                   HttpServletResponse response) throws IOException {
        
        // Parse range header: "bytes=start-end"
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        long start = Long.parseLong(ranges[0]);
        long end = ranges.length > 1 && !ranges[1].isEmpty() 
                   ? Long.parseLong(ranges[1]) 
                   : fileSize - 1;
        
        // Validate range
        if (start > end || start < 0 || end >= fileSize) {
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize);
            return;
        }
        
        long contentLength = end - start + 1;
        
        // Set response headers
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        response.setContentType("audio/mpeg");
        response.setContentLengthLong(contentLength);
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setHeader(HttpHeaders.CONTENT_RANGE, 
                          String.format("bytes %d-%d/%d", start, end, fileSize));
        
        // Stream the requested byte range
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            randomAccessFile.seek(start);
            byte[] buffer = new byte[CHUNK_SIZE];
            long bytesToRead = contentLength;
            while (bytesToRead > 0) {
                int bytesRead = randomAccessFile.read(buffer, 0, 
                    (int) Math.min(buffer.length, bytesToRead));
                
                if (bytesRead == -1) {
                    break;
                }
                response.getOutputStream().write(buffer, 0, bytesRead);
                bytesToRead -= bytesRead;
            }
            response.getOutputStream().flush();
        }
        
        logger.debug("Streamed partial file: {} (bytes {}-{}/{})", 
                    file.getName(), start, end, fileSize);
    }
    
    /**
     * Sanitize filename for download
     * Removes invalid characters from filename.
     * filename original filename
     * converted  filename to store in database
     */
    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}
