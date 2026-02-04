package com.example.taxone.service.impl;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.taxone.entity.Token;
import com.example.taxone.repository.TokenRepository;
import com.example.taxone.service.LogoutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        jwt = authHeader.substring(7);
        Token storedToken = tokenRepository.findByToken(jwt).orElse(null);

        if(storedToken != null && !storedToken.getIsExpired() && !storedToken.getIsRevoked()) {
            storedToken.setIsExpired(true);
            storedToken.setIsRevoked(true);
            tokenRepository.save(storedToken);
        }
    }

}
