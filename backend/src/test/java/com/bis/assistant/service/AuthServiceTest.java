package com.bis.assistant.service;

import com.bis.assistant.dto.AuthResponse;
import com.bis.assistant.dto.LoginRequest;
import com.bis.assistant.dto.SignupRequest;
import com.bis.assistant.dto.UserProfileDTO;
import com.bis.assistant.model.User;
import com.bis.assistant.repository.UserRepository;
import com.bis.assistant.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    void signup_Success() {
        SignupRequest request = new SignupRequest("Rajesh Kumar", "rajesh@example.com", "Secret@123");

        when(userRepository.existsByEmailIgnoreCase("rajesh@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret@123")).thenReturn("hashedPassword123");

        User savedUser = new User("Rajesh Kumar", "rajesh@example.com", "hashedPassword123");
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateToken("rajesh@example.com", 1L)).thenReturn("mock.jwt.token");

        AuthResponse response = authService.signup(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getEmail()).isEqualTo("rajesh@example.com");
        assertThat(response.getUser().getFullName()).isEqualTo("Rajesh Kumar");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signup_DuplicateEmailThrowsException() {
        SignupRequest request = new SignupRequest("Rajesh Kumar", "rajesh@example.com", "Secret@123");

        when(userRepository.existsByEmailIgnoreCase("rajesh@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signup_ShortPasswordThrowsException() {
        SignupRequest request = new SignupRequest("Rajesh Kumar", "rajesh@example.com", "123");

        when(userRepository.existsByEmailIgnoreCase("rajesh@example.com")).thenReturn(false);

        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password must be at least 6 characters");
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest("rajesh@example.com", "Secret@123");

        User user = new User("Rajesh Kumar", "rajesh@example.com", "hashedPassword123");
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase("rajesh@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Secret@123", "hashedPassword123")).thenReturn(true);
        when(jwtTokenProvider.generateToken("rajesh@example.com", 1L)).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUser().getEmail()).isEqualTo("rajesh@example.com");
    }

    @Test
    void login_InvalidPasswordThrowsBadCredentials() {
        LoginRequest request = new LoginRequest("rajesh@example.com", "WrongPassword");

        User user = new User("Rajesh Kumar", "rajesh@example.com", "hashedPassword123");
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase("rajesh@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword123")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void login_NonExistentUserThrowsBadCredentials() {
        LoginRequest request = new LoginRequest("unknown@example.com", "Secret@123");

        when(userRepository.findByEmailIgnoreCase("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void login_ArchiveUserThrowsBadCredentials() {
        LoginRequest request = new LoginRequest("legacy.anonymous@bis.gov.in", "AnyPassword");

        User archiveUser = new User("Legacy Archive User", "legacy.anonymous@bis.gov.in", "$DISABLED$LOCKED_LEGACY_ARCHIVE_NO_LOGIN$");
        archiveUser.setId(999L);

        when(userRepository.findByEmailIgnoreCase("legacy.anonymous@bis.gov.in")).thenReturn(Optional.of(archiveUser));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void getCurrentUserProfile_Success() {
        User user = new User("Rajesh Kumar", "rajesh@example.com", "hashedPassword123");
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase("rajesh@example.com")).thenReturn(Optional.of(user));

        UserProfileDTO profile = authService.getCurrentUserProfile("rajesh@example.com");

        assertThat(profile).isNotNull();
        assertThat(profile.getId()).isEqualTo(1L);
        assertThat(profile.getFullName()).isEqualTo("Rajesh Kumar");
        assertThat(profile.getEmail()).isEqualTo("rajesh@example.com");
    }
}
