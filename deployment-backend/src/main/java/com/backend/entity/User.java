package com.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"password", "songs"})
@EqualsAndHashCode(of = "id")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(length = 15)
    private String phone;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.LISTENER;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean approved = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean premium = false;
    
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Song> songs = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        
        if (role == Role.ADMIN || role == Role.LISTENER) {
            approved = true;
        }
        
        if (enabled == null) {
            enabled = true;
        }
    }
    
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
    public boolean isApproved() {
        if (role != Role.ARTIST) {
            return true;
        }
        return approved != null && approved;
    }
    
    public boolean isActive() {
        return enabled != null && enabled;
    }
    
    public boolean canUploadContent() {
        return isAdmin() || (role == Role.ARTIST && isApproved() && isActive());
    }
    
    public boolean isPremium() {
        return premium != null && premium;
    }
}
