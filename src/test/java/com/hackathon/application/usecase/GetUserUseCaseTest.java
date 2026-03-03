package com.hackathon.application.usecase;

import com.hackathon.domain.entity.User;
import com.hackathon.domain.exception.UserNotFoundException;
import com.hackathon.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserUseCaseTest {

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    @Mock
    private UserRepository userRepository;

    @Test
    void execute_ShouldReturnUser_WhenUserExists() {
        User user = User.builder().id(1L).name("Test").email("test@test.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = getUserUseCase.execute(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test", result.getName());
        assertEquals("test@test.com", result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void execute_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () ->
                getUserUseCase.execute(99L)
        );

        assertEquals("User not found with id: 99", ex.getMessage());
        verify(userRepository).findById(99L);
    }
}