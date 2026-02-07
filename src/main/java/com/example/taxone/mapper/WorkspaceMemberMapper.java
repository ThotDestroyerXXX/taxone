package com.example.taxone.mapper;

import com.example.taxone.dto.response.WorkspaceMemberResponse;
import com.example.taxone.entity.WorkspaceMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface WorkspaceMemberMapper {

    ObjectMapper mapper = new ObjectMapper();

    @Mapping(source = "user", target = "user")
    WorkspaceMemberResponse toResponse(WorkspaceMember workspaceMember);

    WorkspaceMember toEntity(WorkspaceMemberResponse workspaceMemberResponse);

    default List<WorkspaceMemberResponse> toResponseList(List<WorkspaceMember> workspaceMember) {
        if(workspaceMember == null) return List.of();

        return workspaceMember.stream()
                .map(this::toResponse)
                .toList();
    }
}
