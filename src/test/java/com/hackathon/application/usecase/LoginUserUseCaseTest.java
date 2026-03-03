package com.hackathon.application.usecase;

import com.hackathon.domain.entity.User;
import com.hackathon.domain.exception.AuthenticationException;
import com.hackathon.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    @InjectMocks
    private LoginUserUseCase loginUserUseCase;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenUseCase tokenUseCase;

    @Test
    void execute_ShouldReturnToken_WhenCredentialsAreValid() {
        User user = User.builder().id(1L).email("test@test.com").password("encodedPassword").build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(tokenUseCase.generateToken(user)).thenReturn("valid.jwt.token");

        String token = loginUserUseCase.execute("test@test.com", "rawPassword");

        assertEquals("valid.jwt.token", token);
        verify(tokenUseCase).generateToken(user);
    }

    @Test
    void execute_ShouldThrowAuthenticationException_WhenUserNotFound() {
        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        AuthenticationException ex = assertThrows(AuthenticationException.class, () ->
                loginUserUseCase.execute("notfound@test.com", "anyPassword")
        );

        assertEquals("Invalid username or password", ex.getMessage());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(tokenUseCase, never()).generateToken(any());
    }

    @Test
    void execute_ShouldThrowAuthenticationException_WhenPasswordIsWrong() {
        User user = User.builder().id(1L).email("test@test.com").password("encodedPassword").build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        AuthenticationException ex = assertThrows(AuthenticationException.class, () ->
                loginUserUseCase.execute("test@test.com", "wrongPassword")
        );

        assertEquals("Invalid username or password", ex.getMessage());
        verify(tokenUseCase, never()).generateToken(any());
    }
}