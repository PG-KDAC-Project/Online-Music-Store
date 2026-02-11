package com.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
   
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            // REMOVE: .allowedOrigins("*") 
            // ADD: Your specific frontend URLs (Netlify + Localhost for testing)
            .allowedOrigins(
                "https://online-music-store.netlify.app",
                "https://www.online-music-store.netlify.app",
                "http://localhost:5173", // If using Vite locally
                "http://localhost:3000"  // If using CRA locally
            )
            .allowedOriginPatterns("https://*.netlify.app")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true); // This requires specific origins, not "*"
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
