package com.example.taxone.service;

import com.example.taxone.dto.request.UserRequest;
import com.example.taxone.dto.response.UserResponse;

public interface UserService {
    UserResponse getOwnProfile();
    UserResponse updateOwnProfile(UserRequest userRequest);
    void deleteOwnProfile();
}
