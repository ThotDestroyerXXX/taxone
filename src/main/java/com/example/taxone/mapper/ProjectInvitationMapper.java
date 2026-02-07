package com.example.taxone.mapper;

import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.dto.response.WorkspaceInvitationResponse;
import com.example.taxone.entity.ProjectInvitation;
import com.example.taxone.entity.WorkspaceInvitation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectInvitationMapper {
    ObjectMapper mapper = new ObjectMapper();

    @Mapping(source = "invitedBy", target = "invitedBy")
    ProjectInvitationResponse toResponse(ProjectInvitation projectInvitation);

    ProjectInvitation toEntity(ProjectInvitationResponse projectInvitationResponse);

    default List<ProjectInvitationResponse> toResponseList(List<ProjectInvitation> projectInvitations) {
        if(projectInvitations == null) return List.of();

        return projectInvitations.stream()
                .map(this::toResponse)
                .toList();
    }
}
