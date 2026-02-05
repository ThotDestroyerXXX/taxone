package com.example.taxone.mapper;


import com.example.taxone.dto.response.WorkspaceInvitationResponse;
import com.example.taxone.dto.response.WorkspaceMemberResponse;
import com.example.taxone.dto.response.WorkspaceResponse;
import com.example.taxone.entity.Workspace;
import com.example.taxone.entity.WorkspaceInvitation;
import com.example.taxone.entity.WorkspaceMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkspaceInvitationMapper {
    ObjectMapper mapper = new ObjectMapper();

    WorkspaceInvitationResponse toResponse(WorkspaceInvitation workspaceInvitation);

    WorkspaceInvitation toEntity(WorkspaceInvitationResponse workspaceInvitationResponse);

    default List<WorkspaceInvitationResponse> toResponseList(List<WorkspaceInvitation> workspaceInvitations) {
        if(workspaceInvitations == null) return List.of();

        return workspaceInvitations.stream()
                .map(this::toResponse)
                .toList();
    }
}
