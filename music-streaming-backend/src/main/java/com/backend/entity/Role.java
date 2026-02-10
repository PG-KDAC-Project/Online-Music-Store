package com.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Role Enum for Music Streaming Application
 * 
 * Defines the user roles in the system with their respective authorities.
 * Used for authentication and authorization with Spring Security.
 * 
 * @author Music Streaming Team
 * @since 1.0
 */
public enum Role {
    
    /**
     * Administrator role - Full system access
     * Can manage users, songs, albums, artists, and system configuration
     */
    ADMIN("ROLE_ADMIN", "Administrator"),
    
    /**
     * Artist role - Content creator access
     * Can upload and manage own songs, albums, and artist profile
     */
    ARTIST("ROLE_ARTIST", "Artist"),
    
    /**
     * Listener role - Standard user access
     * Can stream music, create playlists, and manage own profile
     */
    LISTENER("ROLE_LISTENER", "Listener");
    
    private final String authority;
    private final String displayName;
    
    /**
     * Constructor for Role enum
     * 
     * @param authority Spring Security authority (e.g., "ROLE_ADMIN")
     * @param displayName Human-readable role name
     */
    Role(String authority, String displayName) {
        this.authority = authority;
        this.displayName = displayName;
    }
    
    /**
     * Gets the Spring Security authority string
     * 
     * @return authority string (e.g., "ROLE_ADMIN")
     */
    public String getAuthority() {
        return authority;
    }
    
    /**
     * Returns the enum name for JSON serialization
     * This ensures JSON output is "ADMIN", "ARTIST", "LISTENER"
     * 
     * @return enum name
     */
    @JsonValue
    public String getName() {
        return this.name();
    }
    
    /**
     * Custom deserializer for JSON input
     * Accepts both formats: "ADMIN" or "ROLE_ADMIN"
     * 
     * @param value the input string from JSON
     * @return the corresponding Role enum
     * @throws IllegalArgumentException if value is not recognized
     */
    @JsonCreator
    public static Role fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return LISTENER; // Default to LISTENER if no role specified
        }
        
        String normalized = value.trim().toUpperCase();
        
        // Handle "ROLE_ADMIN" format (strip ROLE_ prefix)
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }
        
        // Try to match enum name
        try {
            return Role.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Invalid role: '" + value + "'. Valid values are: ADMIN, ARTIST, LISTENER " +
                "(or ROLE_ADMIN, ROLE_ARTIST, ROLE_LISTENER)"
            );
        }
    }
    
    /**
     * Gets the human-readable display name
     * 
     * @return display name (e.g., "Administrator")
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Checks if the role has admin privileges
     * 
     * @return true if role is ADMIN, false otherwise
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * Checks if the role has artist privileges
     * 
     * @return true if role is ADMIN or ARTIST, false otherwise
     */
    public boolean isArtist() {
        return this == ADMIN || this == ARTIST;
    }
    
    /**
     * Checks if the role has listener privileges
     * 
     * @return true for all roles (base privilege)
     */
    public boolean isListener() {
        return true;
    }
    
    /**
     * Converts authority string to Role enum
     * 
     * @param authority Spring Security authority string
     * @return corresponding Role enum
     * @throws IllegalArgumentException if authority is not recognized
     */
    public static Role fromAuthority(String authority) {
        if (authority == null) {
            throw new IllegalArgumentException("Authority cannot be null");
        }
        
        for (Role role : values()) {
            if (role.authority.equals(authority)) {
                return role;
            }
        }
        
        throw new IllegalArgumentException("Unknown authority: " + authority);
    }
    
    /**
     * Returns the authority string for Spring Security integration
     * 
     * @return authority string
     */
    @Override
    public String toString() {
        return authority;
    }
}
