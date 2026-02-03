package com.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongResponse {
    
    private Long id;
    private String title;
    private String artistName;
    private Long artistId;
    private String album;
    private String genre;
    private String language;
    private Integer duration;
    private String formattedDuration;
    private String filePath;
    private String coverImagePath;
    private Long playCount;
    private Long likeCount;
    private LocalDateTime createdAt;
}
