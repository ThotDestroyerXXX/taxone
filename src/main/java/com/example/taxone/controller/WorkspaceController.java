package com.example.taxone.controller;


import com.example.taxone.dto.request.ProjectRequest;
import com.example.taxone.dto.request.WorkspaceInvitationRequest;
import com.example.taxone.dto.request.WorkspaceMemberRoleRequest;
import com.example.taxone.dto.request.WorkspaceRequest;
import com.example.taxone.dto.response.ProjectResponse;
import com.example.taxone.dto.response.WorkspaceInvitationResponse;
import com.example.taxone.dto.response.WorkspaceMemberResponse;
import com.example.taxone.dto.response.WorkspaceResponse;
import com.example.taxone.entity.WorkspaceInvitation;
import com.example.taxone.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkspaceResponse> createWorkspace(@RequestBody @Valid WorkspaceRequest workspaceRequest) {
        return ResponseEntity.ok(workspaceService.createWorkspace(workspaceRequest));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceResponse>> getAllWorkspaces() {
        List<WorkspaceResponse> workspaces = workspaceService.getWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> getWorkspace(@PathVariable String workspaceId) {
        WorkspaceResponse response = workspaceService.getWorkspace(workspaceId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(
            @PathVariable String workspaceId,
            @RequestBody @Valid WorkspaceRequest workspaceRequest) {
        WorkspaceResponse response = workspaceService.updateWorkspace(workspaceId, workspaceRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workspaceId}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable String workspaceId) {
        workspaceService.deleteWorkspace(workspaceId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{workspaceId}/restore")
    public ResponseEntity<WorkspaceResponse> restoreWorkspace(@PathVariable String workspaceId) {
        WorkspaceResponse response = workspaceService.restoreWorkspace(workspaceId);
        return ResponseEntity.ok(response);
    }

    // TODO: UPDATE LOGO

    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<List<WorkspaceMemberResponse>> getMembers(@PathVariable String workspaceId) {
        List<WorkspaceMemberResponse> members = workspaceService.getMembers(workspaceId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{workspaceId}/members")
    public ResponseEntity<WorkspaceInvitationResponse> inviteMember(
            @PathVariable String workspaceId,
            @RequestBody @Valid WorkspaceInvitationRequest invitationRequest) {
        WorkspaceInvitationResponse response = workspaceService.inviteMember(workspaceId, invitationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workspaceId}/members/{memberId}")
    public ResponseEntity<WorkspaceMemberResponse> getMember(@PathVariable String workspaceId,
                                                             @PathVariable String memberId) {
        WorkspaceMemberResponse response = workspaceService.getMember(workspaceId, memberId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{workspaceId}/members/{memberId}")
    public ResponseEntity<WorkspaceMemberResponse> updateMemberRole(
            @PathVariable String workspaceId,
            @PathVariable String memberId,
            @RequestBody @Valid WorkspaceMemberRoleRequest roleRequest) {
        WorkspaceMemberResponse response = workspaceService.updateMemberRole(workspaceId, memberId, roleRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workspaceId}/members/{userId}")
    public ResponseEntity<WorkspaceMemberResponse> deleteMember(@PathVariable String workspaceId,
                                                                @PathVariable String userId) {
        workspaceService.deleteMember(workspaceId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{workspaceId}/invitations")
    public ResponseEntity<List<WorkspaceInvitationResponse>> getPendingInvites(@PathVariable String workspaceId) {
        List<WorkspaceInvitationResponse> invitations = workspaceService.getPendingInvites(workspaceId);
        return ResponseEntity.ok(invitations);
    }

    @DeleteMapping("/{workspaceId}/invitations/{invitationId}")
    public ResponseEntity<Void> cancelInvite(@PathVariable String workspaceId, @PathVariable String invitationId) {
        workspaceService.cancelInvite(workspaceId, invitationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{workspaceId}/projects")
    public ResponseEntity<ProjectResponse> createProject(@PathVariable String workspaceId,
                                                         @RequestBody @Valid ProjectRequest projectRequest) {
        ProjectResponse response = workspaceService.createProject(workspaceId, projectRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workspaceId}/projects")
    public ResponseEntity<List<ProjectResponse>> getProjects(@PathVariable String workspaceId) {
        List<ProjectResponse> response = workspaceService.getProjects(workspaceId);
        return ResponseEntity.ok(response);
    }


}
