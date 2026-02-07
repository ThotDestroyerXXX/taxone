package com.example.taxone.service;

import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.dto.response.WorkspaceInvitationResponse;

public interface InvitationService {

    ProjectInvitationResponse acceptProjectInvite(String projectId);
    WorkspaceInvitationResponse acceptWorkspaceInvite(String workspaceId);
    ProjectInvitationResponse declineProjectInvite(String projectId);
    WorkspaceInvitationResponse declineWorkspaceInvite(String workspaceId);
}
