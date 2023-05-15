package com.itskillsnow.apigateway.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;

@Component
public class JwtUtil {


    public static final String JWT_SC = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";


    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    public Claims extractClaims(final String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    public List<String> extractRoles(final String token) {
        Claims claims = extractClaims(token);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(claims.get("roles"), new TypeReference<>() {});
    }

    public String extractUsername(final String token) {
        Claims claims = extractClaims(token);
        return claims.get("username", String.class);
    }


    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SC);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
