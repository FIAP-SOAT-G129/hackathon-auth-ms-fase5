package com.hackathon.application.usecase;

import com.hackathon.domain.entity.User;
import com.hackathon.domain.exception.UserAlreadyExistsException;
import com.hackathon.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_ShouldRegisterUser_WhenDataIsValid() {
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = registerUserUseCase.execute(user);

        assertNotNull(result);
        assertEquals("encodedPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void execute_ShouldThrowException_WhenUsernameAlreadyExists() {
        User user = User.builder().username("existing").build();
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> registerUserUseCase.execute(user));
    }
}
