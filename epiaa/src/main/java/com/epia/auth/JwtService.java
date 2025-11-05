package com.epia.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key secretKey = Keys.hmacShaKeyFor(
            "THIS_IS_A_SECRET_KEY_FOR_JWT_256_BIT_KEY_EXAMPLE".getBytes()
    );

    private final long EXP = 1000L * 60 * 60 * 4;

    public String create(Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXP))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("INVALID_TOKEN");
        }
    }
}