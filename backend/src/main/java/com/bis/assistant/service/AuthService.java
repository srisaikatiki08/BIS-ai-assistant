package com.bis.assistant.service;

import com.bis.assistant.dto.AuthResponse;
import com.bis.assistant.dto.LoginRequest;
import com.bis.assistant.dto.SignupRequest;
import com.bis.assistant.dto.UserProfileDTO;
import com.bis.assistant.model.User;
import com.bis.assistant.repository.UserRepository;
import com.bis.assistant.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Registers a new user with duplicate-email check, BCrypt password hashing, and returns JWT token.
     */
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Signup request body cannot be null.");
        }

        String cleanEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        String cleanFullName = request.getFullName() != null ? request.getFullName().trim() : "";

        if (cleanEmail.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }

        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            logger.warn("Signup attempt with already registered email: {}", cleanEmail);
            throw new IllegalArgumentException("An account with email '" + cleanEmail + "' already exists.");
        }

        String rawPassword = request.getPassword();
        if (rawPassword == null || rawPassword.trim().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        User newUser = new User(cleanFullName, cleanEmail, passwordHash);
        User savedUser = userRepository.save(newUser);

        logger.info("New user successfully registered with id: {}", savedUser.getId());

        String token = jwtTokenProvider.generateToken(savedUser.getEmail(), savedUser.getId());
        UserProfileDTO userProfile = mapToProfileDTO(savedUser);

        return AuthResponse.success(token, userProfile, "User registered successfully.");
    }

    /**
     * Authenticates an existing user via email and password, issuing a JWT token upon verification.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Login request body cannot be null.");
        }

        String cleanEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        String rawPassword = request.getPassword();

        if (cleanEmail.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        User user = userRepository.findByEmailIgnoreCase(cleanEmail)
                .orElseThrow(() -> {
                    logger.warn("Login attempt for non-existent email: {}", cleanEmail);
                    return new BadCredentialsException("Invalid email or password.");
                });

        if (cleanEmail.equalsIgnoreCase("legacy.anonymous@bis.gov.in") ||
                user.getPasswordHash().startsWith("$DISABLED$") ||
                user.getPasswordHash().startsWith("*LOCKED*")) {
            logger.warn("Login attempt rejected for locked archive account: {}", cleanEmail);
            throw new BadCredentialsException("Invalid email or password.");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            logger.warn("Invalid password attempt for user id: {}", user.getId());
            throw new BadCredentialsException("Invalid email or password.");
        }

        logger.info("User id: {} successfully authenticated.", user.getId());

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getId());
        UserProfileDTO userProfile = mapToProfileDTO(user);

        return AuthResponse.success(token, userProfile, "Login successful.");
    }

    /**
     * Retrieves the profile information for an authenticated user email.
     */
    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be empty.");
        }

        User user = userRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));

        return mapToProfileDTO(user);
    }

    private UserProfileDTO mapToProfileDTO(User user) {
        return new UserProfileDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
