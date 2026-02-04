package com.example.taxone.mapper;

import com.example.taxone.dto.response.WorkspaceResponse;
import com.example.taxone.entity.Workspace;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {
    ObjectMapper mapper = new ObjectMapper();

    WorkspaceResponse toResponse(Workspace workspace);

    Workspace toEntity(WorkspaceResponse workspaceResponse);

    default List<WorkspaceResponse> toResponseList(List<Workspace> workspaces) {
        if(workspaces == null) return List.of();

        return workspaces.stream()
                .map(this::toResponse)
                .toList();
    }
}
