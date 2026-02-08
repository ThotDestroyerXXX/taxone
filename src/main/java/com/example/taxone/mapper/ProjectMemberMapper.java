package com.example.taxone.mapper;

import com.example.taxone.dto.response.ProjectMemberResponse;
import com.example.taxone.entity.ProjectMember;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectMemberMapper {

    ProjectMemberResponse toResponse(ProjectMember member);

    ProjectMember toEntity(ProjectMemberResponse memberResponse);

    default List<ProjectMemberResponse> toResponseList(List<ProjectMember> members) {
        if(members == null) return List.of();

        return members.stream()
                .map(this::toResponse)
                .toList();
    }
}
