package com.backend.config;

import com.backend.entity.Role;
import com.backend.entity.User;
import com.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.admin.email:arya@scotify.com}")
    private String adminEmail;
    
    @Value("${app.admin.password:Arya@123}")
    private String adminPassword;
    
    @Value("${app.admin.name:System Admin}")
    private String adminName;
    
    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .name(adminName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .enabled(true)
                    .approved(true)
                    .premium(true)
                    .build();
            
            userRepository.save(admin);
            log.info("Default admin user created: {}", adminEmail);
        }
    }
}