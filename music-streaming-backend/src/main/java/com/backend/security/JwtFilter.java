package com.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        boolean validHeader = authHeader != null && authHeader.startsWith("Bearer");
        Authentication auth = null;
        
        if (validHeader) {
            String token = authHeader.replace("Bearer", "").trim();
            auth = jwtUtil.validateToken(token);
        }
        
        if (auth != null && SecurityContextHolder.getContext().getAuthentication() == null)
            SecurityContextHolder.getContext().setAuthentication(auth);
        
        filterChain.doFilter(request, response);
    }
}
