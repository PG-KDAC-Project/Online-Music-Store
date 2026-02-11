package com.backend.exception;

/**
 * File Storage Exception
 * 
 * Thrown when there's an error storing files to the filesystem.
 * 
 * @author Music Streaming Team
 * @since 1.0
 */
public class FileStorageException extends RuntimeException {
    
    public FileStorageException(String message) {
        super(message);
    }
    
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
