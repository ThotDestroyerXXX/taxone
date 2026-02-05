package com.example.taxone.mapper;

import com.example.taxone.dto.response.UserResponse;
import com.example.taxone.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ObjectMapper mapper = new ObjectMapper();

    UserResponse toResponse(User user);

    User toEntity(UserResponse userResponse);
}
