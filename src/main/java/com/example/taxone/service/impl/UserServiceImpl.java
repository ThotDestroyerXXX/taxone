package com.example.taxone.service.impl;


import com.example.taxone.dto.request.UserRequest;
import com.example.taxone.dto.response.UserResponse;
import com.example.taxone.entity.User;
import com.example.taxone.mapper.UserMapper;
import com.example.taxone.repository.UserRepository;
import com.example.taxone.service.UserService;
import com.example.taxone.util.AuthenticationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final AuthenticationHelper authenticationHelper;

    @Override
    public UserResponse getOwnProfile() {
        User user = authenticationHelper.getCurrentUser();

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateOwnProfile(UserRequest userRequest) {
        User user = authenticationHelper.getCurrentUser();

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(String.valueOf(userRequest.getPhoneNumber()));
        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public void deleteOwnProfile() {
        User user = authenticationHelper.getCurrentUser();

        user.setIsActive(false);
        userRepository.save(user);
    }
}
