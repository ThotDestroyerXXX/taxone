package com.example.taxone.service.impl;


import com.example.taxone.dto.request.UserRequest;
import com.example.taxone.dto.response.UserResponse;
import com.example.taxone.entity.User;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.UserMapper;
import com.example.taxone.repository.UserRepository;
import com.example.taxone.security.CustomUserDetails;
import com.example.taxone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserResponse getOwnProfile() {
        User user = getCurrentUser();

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateOwnProfile(UserRequest userRequest) {
        User user = getCurrentUser();

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(String.valueOf(userRequest.getPhoneNumber()));
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public void deleteOwnProfile() {
        User user = getCurrentUser();

        user.setIsActive(false);
        userRepository.save(user);
    }

    // helper methods
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        throw new IllegalStateException("User not authenticated");
    }
}
