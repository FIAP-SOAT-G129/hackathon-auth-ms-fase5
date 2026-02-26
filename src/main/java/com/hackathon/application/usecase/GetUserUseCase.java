package com.hackathon.application.usecase;

import com.hackathon.domain.entity.User;
import com.hackathon.domain.exception.UserNotFoundException;
import com.hackathon.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserUseCase {

    private final UserRepository userRepository;

    public User execute(Long requestedId, String loggedUsername) {

        User loggedUser = userRepository.findByUsername(loggedUsername)
            .orElseThrow(() ->
                new UserNotFoundException("Logged user not found with username: " + loggedUsername)
            );

        if (!loggedUser.getId().equals(requestedId)) {
            throw new AccessDeniedException("You are not allowed to access this user");
        }

        return loggedUser;
    }

    public User execute(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() ->
                new UserNotFoundException("User not found with username: " + username)
            );
    }
}