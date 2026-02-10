package com.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    private Key jwtKey;
    
    @PostConstruct
    public void init() {
        jwtKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    public String createToken(Authentication auth) {
        String subject = auth.getName();
        String roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        
        String token = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("role", roles)
                .signWith(jwtKey, SignatureAlgorithm.HS256)
                .compact();
        return token;
    }
    
    public Authentication validateToken(String token) {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(jwtKey).build();
        Claims claims = parser
                .parseClaimsJws(token)
                .getBody();
        String email = claims.getSubject();
        String roles = (String) claims.get("role");
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
        return auth;
    }
}
