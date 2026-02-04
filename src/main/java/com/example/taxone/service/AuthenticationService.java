package com.example.taxone.service;

import com.example.taxone.entity.Token;
import com.example.taxone.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    User registerUser(String name, String email, String password);
    UserDetails authenticate(String email, String password);
    String generateToken(UserDetails userDetails);
    UserDetails validateToken(String token);
    Token saveUserToken(UserDetails user, String token);
    void revokeAllUserTokens(UserDetails user);
}
