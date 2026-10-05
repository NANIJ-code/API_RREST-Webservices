package com.apirestjava.example.apirest.service;

import com.apirestjava.example.apirest.dto.*;
import com.apirestjava.example.apirest.entity.RefreshToken;
import com.apirestjava.example.apirest.entity.User;
import com.apirestjava.example.apirest.exception.AuthenticationFailedException;
import com.apirestjava.example.apirest.exception.ConflictException;
import com.apirestjava.example.apirest.repository.RefreshTokenRepository;
import com.apirestjava.example.apirest.repository.UserRepository;
import com.apirestjava.example.apirest.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;

@Service
public class AuthService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long refreshExpirationMs;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       @Value("${jwt.refresh-expiration-ms:604800000}") long refreshExpirationMs) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Ce nom d'utilisateur est déjà utilisé");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Cet email est déjà utilisé");
        }

        User user = new User(username, passwordEncoder.encode(request.getPassword()), email);
        user = userRepository.save(user);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Même message pour "utilisateur inconnu" et "mauvais mot de passe" :
        // on ne révèle pas quels noms d'utilisateur existent.
        User user = userRepository.findByUsername(request.getUsername().trim())
                .orElseThrow(() -> new AuthenticationFailedException("Identifiants invalides"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationFailedException("Identifiants invalides");
        }
        return buildTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new AuthenticationFailedException("Refresh token invalide"));

        if (stored.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(stored);
            throw new AuthenticationFailedException("Refresh token expiré, reconnectez-vous");
        }

        User user = stored.getUser();
        refreshTokenRepository.delete(stored); // rotation : l'ancien ne peut plus servir
        return buildTokens(user);
    }

    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(refreshTokenRepository::delete);
    }

    private AuthResponse buildTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken refreshToken = new RefreshToken(
                newTokenValue(),
                user,
                LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000L));
        refreshTokenRepository.save(refreshToken);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtService.getAccessExpirationSeconds(),
                user.getId(),
                user.getUsername());
    }

    private String newTokenValue() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}