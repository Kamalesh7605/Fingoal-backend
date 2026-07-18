package com.fingoal.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessTokenTtlMinutes;
    private final long refreshTokenTtlDays;

    public JwtService(
            @Value("${fingoal.jwt.secret}") String secret,
            @Value("${fingoal.jwt.access-token-ttl-minutes}") long accessTokenTtlMinutes,
            @Value("${fingoal.jwt.refresh-token-ttl-days}") long refreshTokenTtlDays
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtlMinutes = accessTokenTtlMinutes;
        this.refreshTokenTtlDays = refreshTokenTtlDays;
    }

    public String generateAccessToken(UserDetails user) {
        return buildToken(user.getUsername(), "access", Instant.now().plus(accessTokenTtlMinutes, ChronoUnit.MINUTES));
    }

    public String generateRefreshToken(UserDetails user) {
        return buildToken(user.getUsername(), "refresh", Instant.now().plus(refreshTokenTtlDays, ChronoUnit.DAYS));
    }

    private String buildToken(String subject, String type, Instant expiry) {
        return Jwts.builder()
                .subject(subject)
                .claim("type", type)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public boolean isTokenValid(String token, UserDetails user) {
        try {
            final String username = extractUsername(token);
            return username.equals(user.getUsername()) && !isExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        return resolver.apply(claims);
    }
}
