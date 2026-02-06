package com.example.taxone.mapper;

import com.example.taxone.dto.response.ProjectResponse;
import com.example.taxone.dto.response.WorkspaceMemberResponse;
import com.example.taxone.entity.Project;
import com.example.taxone.entity.WorkspaceMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ObjectMapper mapper = new ObjectMapper();

    ProjectResponse toResponse(Project project);

    Project toEntity(ProjectResponse projectResponse);

    default List<ProjectResponse> toResponseList(List<Project> projects) {
        if(projects == null) return List.of();

        return projects.stream()
                .map(this::toResponse)
                .toList();
    }
}
