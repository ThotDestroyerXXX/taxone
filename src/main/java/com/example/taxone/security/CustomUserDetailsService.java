package com.example.taxone.security;


import com.example.taxone.entity.User;
import com.example.taxone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found with email: " + email)
                );

        if (!user.getIsActive()) { // Check your custom 'active' field
            throw new InternalAuthenticationServiceException("User account is inactive. Please activate your account.");
        }

        return new CustomUserDetails(user);
    }
}
