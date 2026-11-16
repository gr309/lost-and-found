package com.learn.lostfound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.learn.lostfound.controller.UserController;
import com.learn.lostfound.entity.User;
import com.learn.lostfound.repository.UserRepository;
import com.learn.lostfound.service.UserService;

class SignupTests {
    private UserRepository repository;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        mvc = MockMvcBuilders.standaloneSetup(new UserController(new UserService(repository))).build();
    }

    @Test
    void savesUserWithHashedPasswordAndReturnsCreatedWithoutPassword() throws Exception {
        when(repository.saveAndFlush(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertThat(user.getName()).isEqualTo("Alice");
            assertThat(user.getEmail()).isEqualTo("alice@example.com");
            String hash = (String) new DirectFieldAccessor(user).getPropertyValue("password");
            assertThat(hash).isNotEqualTo("password123");
            assertThat(Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8()
                    .matches("password123", hash)).isTrue();
            return user;
        });

        mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":" Alice ","email":"Alice@Example.com","password":"password123"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
        verify(repository).saveAndFlush(any(User.class));
    }

    @Test
    void rejectsInvalidSignupBeforeAccessingRepository() throws Exception {
        for (String body : new String[] {
                "{}",
                "{\"name\":\" \",\"email\":\"alice@example.com\",\"password\":\"password123\"}",
                "{\"name\":\"Alice\",\"email\":\"invalid\",\"password\":\"password123\"}",
                "{\"name\":\"Alice\",\"email\":\"alice@example.com\",\"password\":\"short\"}"
        }) {
            mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsDuplicateEmail() throws Exception {
        when(repository.existsByEmail("alice@example.com")).thenReturn(true);
        signupAlice().andExpect(status().isConflict());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void handlesConcurrentDuplicateSignup() throws Exception {
        when(repository.existsByEmail("alice@example.com")).thenReturn(false, true);
        when(repository.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("Duplicate email"));
        signupAlice().andExpect(status().isConflict());
    }

    private org.springframework.test.web.servlet.ResultActions signupAlice() throws Exception {
        return mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name":"Alice","email":"alice@example.com","password":"password123"}
                        """));
    }
}
