package com.hackathon.adapter.in.controller;

import com.hackathon.domain.exception.AuthenticationException;
import com.hackathon.domain.exception.UserAlreadyExistsException;
import com.hackathon.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleUserAlreadyExists_ShouldReturn409() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User already exists");

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleUserAlreadyExists(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody().get("error"));
    }

    @Test
    void handleAuthenticationException_ShouldReturn401() {
        AuthenticationException ex = new AuthenticationException("Invalid username or password");

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleAuthenticationException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody().get("error"));
    }

    @Test
    void handleUserNotFound_ShouldReturn404() {
        UserNotFoundException ex = new UserNotFoundException("User not found with id: 1");

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleUserNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found with id: 1", response.getBody().get("error"));
    }

    @Test
    void handleBadCredentials_ShouldReturn401() {
        BadCredentialsException ex = new BadCredentialsException("Invalid user id");

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleBadCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid user id", response.getBody().get("Unauthorized"));
    }
}