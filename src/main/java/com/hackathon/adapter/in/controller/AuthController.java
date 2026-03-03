package com.hackathon.adapter.in.controller;

import com.hackathon.adapter.in.dto.*;
import com.hackathon.application.usecase.GetUserUseCase;
import com.hackathon.application.usecase.LoginUserUseCase;
import com.hackathon.application.usecase.RegisterUserUseCase;
import com.hackathon.domain.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final GetUserUseCase getUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        User registeredUser = registerUserUseCase.execute(user);
        return ResponseEntity.ok(toResponse(registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = loginUserUseCase.execute(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal String userId) {
        User user = getUserUseCase.execute(Long.valueOf(userId));
        return ResponseEntity.ok(toResponse(user));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        User user = getUserUseCase.execute(id);
        return ResponseEntity.ok(toResponse(user));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
