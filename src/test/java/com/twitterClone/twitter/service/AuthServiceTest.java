package com.twitterClone.twitter.service;

import com.twitterClone.twitter.config.JwtUtil;
import com.twitterClone.twitter.dto.AuthRequest;
import com.twitterClone.twitter.entity.User;
import com.twitterClone.twitter.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_returnsToken_whenUsernameAndPasswordMatch() {
        AuthRequest request = new AuthRequest();
        request.setUsername("serhat");
        request.setPassword("1234");

        User user = new User();
        user.setId(1L);
        user.setUsername("serhat");
        user.setPassword("1234");

        when(userRepository.findByUsername("serhat")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("serhat")).thenReturn("token-123");

        String token = authService.login(request);

        assertEquals("token-123", token);
        verify(jwtUtil).generateToken("serhat");
    }

    @Test
    void login_throwsUserNotFound_whenUsernameMissing() {
        AuthRequest request = new AuthRequest();
        request.setUsername("unknown");
        request.setPassword("1234");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void login_throwsInvalidPassword_whenPasswordMismatch() {
        AuthRequest request = new AuthRequest();
        request.setUsername("serhat");
        request.setPassword("wrong");

        User user = new User();
        user.setId(1L);
        user.setUsername("serhat");
        user.setPassword("1234");

        when(userRepository.findByUsername("serhat")).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Invalid password", ex.getMessage());
    }
}

