package com.project.fitness.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration:86400000}")
  private int jwtExpirationMs;

  public String getJwtFromHeader(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    return (bearerToken != null && bearerToken.startsWith("Bearer ")) ? bearerToken.substring(7) : null;
  }

  public String generateToken(String userId, String role) {
    return Jwts.builder()
        .subject(userId)
        .claim("roles", List.of(role))
        .claim("role", role)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
        .signWith(key())
        .compact();
  }

  public String getUserIdFromToken(String token) {
    return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
  }

  public Claims getAllClaims(String token) {
    return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
  }

  public boolean validateJwtToken(String token) {
    try {
      Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private SecretKey key() {
    byte[] keyBytes;
    try {
      keyBytes = Decoders.BASE64.decode(jwtSecret);
    } catch (IllegalArgumentException ex) {
      keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    }
    if (keyBytes.length < 32) {
      throw new IllegalStateException("JWT secret must be at least 32 bytes");
    }
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
