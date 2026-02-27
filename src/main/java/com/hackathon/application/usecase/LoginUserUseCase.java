package com.hackathon.application.usecase;

import com.hackathon.domain.entity.User;
import com.hackathon.domain.exception.AuthenticationException;
import com.hackathon.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUseCase tokenUseCase;

    public String execute(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        return tokenUseCase.generateToken(user);
    }
}
