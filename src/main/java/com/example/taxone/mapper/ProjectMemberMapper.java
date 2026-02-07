package com.example.taxone.mapper;

import com.example.taxone.dto.response.ProjectMemberResponse;
import com.example.taxone.entity.ProjectMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectMemberMapper {
    ObjectMapper mapper = new ObjectMapper();

    @Mapping(source = "user", target = "user")
    ProjectMemberResponse toResponse(ProjectMember member);

    ProjectMember toEntity(ProjectMemberResponse memberResponse);

    default List<ProjectMemberResponse> toResponseList(List<ProjectMember> members) {
        if(members == null) return List.of();

        return members.stream()
                .map(this::toResponse)
                .toList();
    }
}
