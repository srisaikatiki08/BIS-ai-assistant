package com.bis.assistant.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        long testExpiration = 3600000; // 1 hour
        jwtTokenProvider = new JwtTokenProvider(testSecret, testExpiration);
    }

    @Test
    void generateAndValidateToken() {
        String email = "priya.sharma@example.com";
        Long userId = 42L;

        String token = jwtTokenProvider.generateToken(email, userId);

        assertThat(token).isNotBlank();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getEmailFromToken(token)).isEqualTo(email);
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(userId);
    }

    @Test
    void validateInvalidTokenReturnsFalse() {
        assertThat(jwtTokenProvider.validateToken("invalid.jwt.token")).isFalse();
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
        assertThat(jwtTokenProvider.validateToken(null)).isFalse();
    }

    @Test
    void expiredTokenReturnsFalse() {
        // Create token provider with 1 millisecond expiration
        JwtTokenProvider shortLivedProvider = new JwtTokenProvider(
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
                -1000 // already expired
        );

        String token = shortLivedProvider.generateToken("user@example.com", 1L);
        assertThat(shortLivedProvider.validateToken(token)).isFalse();
    }
}
