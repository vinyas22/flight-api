package com.proj.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static final long EXPIRATION_TIME = 60 * 60 * 1000; // 1 hour

    private Key getSigningKey() {
        // Use UTF-8 bytes of the secret, length must be >= 32 bytes for HS256
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            System.out.println("🔐 Validating token with secret: " + secret);
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            System.err.println("❌ JWT validation failed: " + e.getMessage());
            return null;
        }
    }


    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            System.err.println("JWT expiration check failed: " + e.getMessage());
            return true;
        }
    }

    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        String username = extractUsername(token);
        return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
