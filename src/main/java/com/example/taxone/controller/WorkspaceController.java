package com.example.taxone.controller;


import com.example.taxone.dto.request.WorkspaceRequest;
import com.example.taxone.dto.response.WorkspaceResponse;
import com.example.taxone.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(@RequestBody @Valid WorkspaceRequest workspaceRequest) {
        return ResponseEntity.ok(workspaceService.createWorkspace(workspaceRequest));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getAllWorkspaces() {
        List<WorkspaceResponse> workspaces = workspaceService.getWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable String id) {
        WorkspaceResponse response = workspaceService.getWorkspace(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(
            @PathVariable String id,
            @RequestBody WorkspaceRequest workspaceRequest) {
        WorkspaceResponse response = workspaceService.updateWorkspace(id, workspaceRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable String id) {
        workspaceService.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<WorkspaceResponse> restoreWorkspace(@PathVariable String id) {
        WorkspaceResponse response = workspaceService.restoreWorkspace(id);
        return ResponseEntity.ok(response);
    }

    // TODO: UPDATE LOGO
}
