package com.example.taxone.mapper;

import com.example.taxone.dto.response.UserResponse;
import com.example.taxone.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    User toEntity(UserResponse userResponse);
}
