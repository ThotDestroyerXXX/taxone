package com.example.taxone.service.impl;

import com.example.taxone.entity.Token;
import com.example.taxone.entity.User;
import com.example.taxone.repository.TokenRepository;
import com.example.taxone.repository.UserRepository;
import com.example.taxone.service.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secretKey;

    private static final Long JWT_EXPIRATION_IN_MS = 86400000L; // 1 day

    @Override
    @Transactional
    public User registerUser(String name, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        String trimmedName = name.trim();
        String[] parts = trimmedName.split("\\s+");

        String firstName = parts[0];
        String lastName = parts.length > 1
                ? String.join(" ", Arrays.copyOfRange(parts, 1, parts.length))
                : "";

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        return userRepository.save(user);
    }

    @Override
    public UserDetails authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        return userDetailsService.loadUserByUsername(email);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_IN_MS))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public UserDetails validateToken(String token) {
        String username = extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    private String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public Token saveUserToken(UserDetails user, String token) {
        Token tokenEntity = Token.builder()
                .user(!user.getUsername().isEmpty() ? userRepository.findByEmail(user.getUsername()).orElse(null) : null)
                .token(token)
                .tokenType(Token.TokenType.BEARER)
                .isExpired(false)
                .isRevoked(false)
                .build();

        tokenRepository.save(tokenEntity);

        return tokenEntity;
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(UserDetails user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokensByUserId(
                userRepository.findByEmail(user.getUsername()).orElseThrow().getId());

        if(validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(t -> {
            t.setIsExpired(true);
            t.setIsRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }
}
