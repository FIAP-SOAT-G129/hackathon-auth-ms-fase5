package com.hackathon.adapter.in.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.adapter.in.dto.LoginRequest;
import com.hackathon.adapter.in.dto.UserRegistrationRequest;
import com.hackathon.application.usecase.GetUserUseCase;
import com.hackathon.application.usecase.LoginUserUseCase;
import com.hackathon.application.usecase.RegisterUserUseCase;
import com.hackathon.application.usecase.TokenUseCase;
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
@WithMockUser
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
    void register_ShouldReturn200() throws Exception {
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
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    void login_ShouldReturnToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password");

        String fakeToken = "eyJhbGciOiJIUzI1NiJ9.fake.token";

        when(loginUserUseCase.execute("test@test.com", "password")).thenReturn(fakeToken);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(fakeToken));
    }

    @Test
    void getUser_ShouldReturnUserById_WhenValidJWT() throws Exception {

        String fakeToken = "valid.jwt.token";
        String userId = "99";

        when(tokenUseCase.extractSub(fakeToken)).thenReturn(userId);

        User user = User.builder()
                .id(1L)
                .name("admin")
                .email("admin@test.com")
                .build();

        when(getUserUseCase.execute(1L)).thenReturn(user);

        mockMvc.perform(get("/auth/users/1")
                        .header("Authorization", "Bearer " + fakeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("admin"))
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    void getUser_ShouldReturnUserById() throws Exception {
        User user = User.builder().id(1L).name("admin").email("admin@test.com").build();
        when(getUserUseCase.execute(1L)).thenReturn(user);

        // Note: Se esta rota for protegida, ela também precisa dos headers ou do @WithMockUser
        mockMvc.perform(get("/auth/users/1")
                        .header("x-user-id", "99") // Simulando um usuário autenticado acessando outro
                        .header("x-user-email", "any@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("admin"));
    }

    @Test
    void me_ShouldReturn403_WhenHeadersAreMissing() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
