package com.itskillsnow.authservice.unitTests;

import com.itskillsnow.authservice.model.Role;
import com.itskillsnow.authservice.service.JwtServiceImpl;
import io.jsonwebtoken.io.Decoders;
import org.springframework.boot.test.context.SpringBootTest;
import com.itskillsnow.authservice.model.User;
import com.itskillsnow.authservice.service.ServiceInterfaces.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceImplTest {
    private JwtService jwtService;
    private User user;
    private String username;

    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        user = new User();
        user.setRoles(List.of(Role.USER));
        username = "testUser";
    }

    @Test
    void testValidateToken() {
        String token = jwtService.generateToken(user, username);
        assertNotNull(token);
        jwtService.validateToken(token);
    }

    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(user, username);
        assertNotNull(token);
        assertTrue(token.length() > 0);

        Map<String, Object> claims = extractClaims(token);
        assertNotNull(claims);
        assertTrue(claims.containsKey("roles"));
        assertTrue(claims.containsKey("username"));
        assertEquals(claims.get("username"), username);
        assertTrue(claims.get("roles") instanceof Iterable);
        assertEquals("USER", ((Iterable<?>) claims.get("roles")).iterator().next());
    }

    @Test
    void testGenerateTokens() {
        User user = new User();
        user.setRoles(List.of(Role.USER));
        String username = "john.doe";

        Map<String, String> tokens = jwtService.generateTokens(user, username);

        assertNotNull(tokens);
        assertEquals(2, tokens.size());
        String accessToken = tokens.get("accessToken");
        assertNotNull(accessToken);
        String[] splitAccessToken = accessToken.split("\\.");
        assertEquals(3, splitAccessToken.length);
        String refreshToken = tokens.get("refreshToken");
        assertNotNull(refreshToken);
        String[] splitRefreshToken = refreshToken.split("\\.");
        assertEquals(3, splitRefreshToken.length);
    }

    @Test
    void testCreateToken() {
        String token = jwtService.generateToken(user, username);
        assertNotNull(token);
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
        assertEquals(username, jws.getBody().getSubject());
    }

    @Test
    void testGetUsernameFromToken() {
        User user = new User();
        user.setRoles(List.of(Role.USER));
        user.setUsername("username");
        String token = jwtService.generateToken(user, username);
        assertEquals(username, jwtService.getUsernameFromToken(token));
    }

    @Test
    void testGetUsernameFromInvalidToken() {
        assertThrows(RuntimeException.class, () -> jwtService.getUsernameFromToken("invalid.token"));
    }

    private Claims extractClaims(final String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}