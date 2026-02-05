package com.example.taxone.service;

import com.example.taxone.dto.request.WorkspaceRequest;
import com.example.taxone.dto.response.WorkspaceResponse;

import java.util.List;

public interface WorkspaceService {
    WorkspaceResponse createWorkspace(WorkspaceRequest workspace);
    List<WorkspaceResponse> getWorkspaces();
    WorkspaceResponse getWorkspace(String id);
    WorkspaceResponse updateWorkspace(String id, WorkspaceRequest workspaceRequest);
    void deleteWorkspace(String id);
    WorkspaceResponse restoreWorkspace(String id);
}
