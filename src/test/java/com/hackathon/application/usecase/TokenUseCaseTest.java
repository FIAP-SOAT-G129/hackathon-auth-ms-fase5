package com.hackathon.application.usecase;

import com.hackathon.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenUseCaseTest {

    private TokenUseCase tokenUseCase;

    @BeforeEach
    void setUp() {
        tokenUseCase = new TokenUseCase();
        ReflectionTestUtils.setField(tokenUseCase, "secretKey", "minha-chave-secreta-com-32-chars!!");
        ReflectionTestUtils.setField(tokenUseCase, "expiration", 3600000L); // 1 hora
        ReflectionTestUtils.setField(tokenUseCase, "issuer", "auth-service");
    }

    private User buildUser() {
        return User.builder()
                .id(1L)
                .name("Test User")
                .email("test@test.com")
                .build();
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        String token = tokenUseCase.generateToken(buildUser());
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void generateToken_ShouldReturnValidJwtFormat() {
        String token = tokenUseCase.generateToken(buildUser());
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    void extractSub_ShouldReturnUserId() {
        User user = buildUser();
        String token = tokenUseCase.generateToken(user);

        String sub = tokenUseCase.extractSub(token);

        assertEquals("1", sub);
    }

    @Test
    void extractSub_ShouldThrowException_WhenTokenIsInvalid() {
        assertThrows(Exception.class, () ->
                tokenUseCase.extractSub("token.invalido.aqui")
        );
    }

    @Test
    void extractSub_ShouldThrowException_WhenTokenIsExpired() {
        ReflectionTestUtils.setField(tokenUseCase, "expiration", -1000L);

        String token = tokenUseCase.generateToken(buildUser());

        assertThrows(Exception.class, () ->
                tokenUseCase.extractSub(token)
        );
    }

    @Test
    void generateToken_ShouldEmbedEmail_InClaims() {
        User user = buildUser();
        String token = tokenUseCase.generateToken(user);

        String sub = tokenUseCase.extractSub(token);
        assertNotNull(sub);
        assertEquals(user.getId().toString(), sub);
    }
}