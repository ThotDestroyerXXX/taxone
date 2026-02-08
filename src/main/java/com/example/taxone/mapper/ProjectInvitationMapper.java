package com.example.taxone.mapper;

import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.entity.ProjectInvitation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectInvitationMapper {
    ProjectInvitationResponse toResponse(ProjectInvitation projectInvitation);

    ProjectInvitation toEntity(ProjectInvitationResponse projectInvitationResponse);

    default List<ProjectInvitationResponse> toResponseList(List<ProjectInvitation> projectInvitations) {
        if(projectInvitations == null) return List.of();

        return projectInvitations.stream()
                .map(this::toResponse)
                .toList();
    }
}
