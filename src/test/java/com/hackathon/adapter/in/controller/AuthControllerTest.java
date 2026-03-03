package com.hackathon.adapter.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.adapter.in.dto.LoginRequest;
import com.hackathon.adapter.in.dto.UserRegistrationRequest;
import com.hackathon.application.usecase.GetUserUseCase;
import com.hackathon.application.usecase.LoginUserUseCase;
import com.hackathon.application.usecase.RegisterUserUseCase;
import com.hackathon.application.usecase.TokenUseCase;
import com.hackathon.domain.entity.User;
import com.hackathon.domain.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @MockBean
    private LoginUserUseCase loginUserUseCase;

    @MockBean
    private GetUserUseCase getUserUseCase;

    @MockBean
    private TokenUseCase tokenUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ShouldReturn200_WhenRequestIsValid() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("test");
        request.setEmail("test@test.com");
        request.setPassword("password");

        User user = User.builder().id(1L).name("test").email("test@test.com").build();
        when(registerUserUseCase.execute(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void register_ShouldReturn400_WhenBodyIsInvalid() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturn200_WhenCredentialsAreValid() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        when(loginUserUseCase.execute("test@test.com", "password")).thenReturn("valid.jwt.token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("valid.jwt.token"));
    }

    @Test
    void login_ShouldReturn400_WhenBodyIsInvalid() throws Exception {
        LoginRequest request = new LoginRequest();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void me_ShouldReturn200_WhenAuthenticated() throws Exception {
        User user = User.builder().id(1L).name("test").email("test@test.com").build();
        when(getUserUseCase.execute(1L)).thenReturn(user);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "1", null, Collections.emptyList()
        );

        mockMvc.perform(get("/auth/me")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void me_ShouldReturn401_WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void getUser_ShouldReturn200_WhenUserExists() throws Exception {
        User user = User.builder().id(1L).name("admin").email("admin@test.com").build();
        when(getUserUseCase.execute(1L)).thenReturn(user);

        mockMvc.perform(get("/auth/users/1")
                        .with(SecurityMockMvcRequestPostProcessors.user("1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    void getUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(getUserUseCase.execute(99L)).thenThrow(new UserNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/auth/users/99")
                        .with(SecurityMockMvcRequestPostProcessors.user("1")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found with id: 99"));
    }
}