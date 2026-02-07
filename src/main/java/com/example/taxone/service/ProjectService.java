package com.example.taxone.service;

import com.example.taxone.dto.request.*;
import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.dto.response.ProjectMemberResponse;
import com.example.taxone.dto.response.ProjectResponse;
import com.example.taxone.dto.response.TaskResponse;

import java.util.List;

public interface ProjectService {
    ProjectResponse getProject(String projectId);
    ProjectResponse updateProject(String projectId, ProjectRequest projectRequest);
    ProjectResponse archiveProject(String projectId);
    ProjectResponse holdProject(String projectId);
    ProjectResponse restoreProject(String projectId);
    List<ProjectMemberResponse> getMembers(String projectId);
    ProjectInvitationResponse inviteMember(String projectId, ProjectInvitationRequest invitationRequest);
    ProjectMemberResponse updateMemberRole(String projectId, String memberId, ProjectMemberRoleRequest roleRequest);
    void deleteMember(String projectId, String memberId);
    ProjectInvitationResponse cancelProjectInvite(String projectId, String invitationId);
    TaskResponse createTask(String projectId, TaskRequest taskRequest);
    List<TaskResponse> getTasks(String projectId);
    List<TaskResponse> filterTasks(String projectId, TaskFilterRequest filter);
}
