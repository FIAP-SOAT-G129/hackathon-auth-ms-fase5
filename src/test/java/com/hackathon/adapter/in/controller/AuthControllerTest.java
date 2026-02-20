package com.hackathon.adapter.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.adapter.in.dto.UserRegistrationRequest;
import com.hackathon.application.usecase.GetUserUseCase;
import com.hackathon.application.usecase.LoginUserUseCase;
import com.hackathon.application.usecase.RegisterUserUseCase;
import com.hackathon.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_ShouldReturn200() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("test");
        request.setEmail("test@test.com");
        request.setPassword("password");

        User user = User.builder().id(1L).username("test").email("test@test.com").build();
        when(registerUserUseCase.execute(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void me_ShouldReturnUserDetails() throws Exception {
        User user = User.builder().id(1L).username("testuser").email("test@test.com").build();
        when(getUserUseCase.execute("testuser")).thenReturn(user);

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }
}
