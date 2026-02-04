package com.example.taxone.service;

import com.example.taxone.dto.request.WorkspaceRequest;
import com.example.taxone.dto.response.WorkspaceResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface WorkspaceService {
    WorkspaceResponse createWorkspace(WorkspaceRequest workspace);
    List<WorkspaceResponse> getWorkspaces();
    WorkspaceResponse getWorkspace(String id);
    WorkspaceResponse updateWorkspace(@PathVariable String id, WorkspaceRequest workspaceRequest);
}
