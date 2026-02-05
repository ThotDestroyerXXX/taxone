package com.example.taxone.service;

import com.example.taxone.dto.request.WorkspaceInvitationRequest;
import com.example.taxone.dto.request.WorkspaceMemberRoleRequest;
import com.example.taxone.dto.request.WorkspaceRequest;
import com.example.taxone.dto.response.WorkspaceInvitationResponse;
import com.example.taxone.dto.response.WorkspaceMemberResponse;
import com.example.taxone.dto.response.WorkspaceResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface WorkspaceService {
    WorkspaceResponse createWorkspace(WorkspaceRequest workspace);
    List<WorkspaceResponse> getWorkspaces();
    WorkspaceResponse getWorkspace(String workspaceId);
    WorkspaceResponse updateWorkspace(String workspaceId, WorkspaceRequest workspaceRequest);
    void deleteWorkspace(String workspaceId);
    WorkspaceResponse restoreWorkspace(String workspaceId);
    List<WorkspaceMemberResponse> getMembers(String workspaceId);
    WorkspaceInvitationResponse inviteMember(String workspaceId, WorkspaceInvitationRequest invitationRequest);
    WorkspaceMemberResponse getMember(String workspaceId, String memberId);
    WorkspaceMemberResponse updateMemberRole(String workspaceId, String memberId, WorkspaceMemberRoleRequest memberRoleRequest);
    void deleteMember(String workspaceId, String memberId);

    List<WorkspaceInvitationResponse> getPendingInvites(String workspaceId);
    void cancelInvite(String workspaceId, String invitationId);
}
