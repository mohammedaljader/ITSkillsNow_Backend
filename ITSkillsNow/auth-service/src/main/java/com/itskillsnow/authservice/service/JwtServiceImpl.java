package com.itskillsnow.authservice.service;

import com.itskillsnow.authservice.entity.User;
import com.itskillsnow.authservice.service.ServiceInterfaces.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtServiceImpl implements JwtService {


    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";


    @Override
    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    @Override
    public String generateToken(User user, String userName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRole());
        claims.put("username", userName);
        return createToken(claims, userName);
    }

    @Override
    public Map<String, String> generateTokens(User user, String username){
        Map<String, String> tokens = new HashMap<>();
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRole());
        claims.put("username", username);
        String accessToken = createToken(claims, username);
        String refreshToken = createRefreshToken(claims, username);
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private String createRefreshToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 2))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
