package com.example.taxone.controller;


import com.example.taxone.dto.request.LoginRequest;
import com.example.taxone.dto.request.RegisterRequest;
import com.example.taxone.dto.response.AuthResponse;
import com.example.taxone.entity.User;
import com.example.taxone.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        UserDetails userDetails = authenticationService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        String token = authenticationService.generateToken(userDetails);

        authenticationService.revokeAllUserTokens(userDetails);

        authenticationService.saveUserToken(userDetails, token);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .expiresIn(86400L) // Token expiry time in seconds
                .build();
        // return the token ONLY!
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        // Register the user
        User user = authenticationService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        log.info("User registered successfully: {}", user.getEmail());

        // Authenticate the newly registered user
        UserDetails userDetails = authenticationService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        // Generate JWT token
        String token = authenticationService.generateToken(userDetails);

        authenticationService.saveUserToken(userDetails, token);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .expiresIn(86400L)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }
}
