package com.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Error Response DTO
 * 
 * Standard error response format for all API errors.
 * 
 * @author Music Streaming Team
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    
    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * HTTP status code
     */
    private int status;
    
    /**
     * HTTP status reason phrase
     */
    private String error;
    
    /**
     * Error message
     */
    private String message;
    
    /**
     * Validation errors (field-specific errors)
     * Only included for validation failures
     */
    private Map<String, String> errors;
}
