package com.example.taxone.mapper;


import com.example.taxone.dto.response.WorkspaceInvitationResponse;
import com.example.taxone.entity.WorkspaceInvitation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses =  {UserMapper.class})
public interface WorkspaceInvitationMapper {
    WorkspaceInvitationResponse toResponse(WorkspaceInvitation workspaceInvitation);

    WorkspaceInvitation toEntity(WorkspaceInvitationResponse workspaceInvitationResponse);

    default List<WorkspaceInvitationResponse> toResponseList(List<WorkspaceInvitation> workspaceInvitations) {
        if(workspaceInvitations == null) return List.of();

        return workspaceInvitations.stream()
                .map(this::toResponse)
                .toList();
    }
}
