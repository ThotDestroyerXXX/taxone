package com.example.taxone.controller;


import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.dto.response.WorkspaceInvitationResponse;
import com.example.taxone.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/{projectInvitationId}/projects/accept")
    public ResponseEntity<ProjectInvitationResponse> acceptProjectInvite(@PathVariable String projectInvitationId) {
        ProjectInvitationResponse response = invitationService.acceptProjectInvite(projectInvitationId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("{workspaceInvitationId}/workspaces/accept")
    public ResponseEntity<WorkspaceInvitationResponse> acceptWorkspaceInvite(@PathVariable String workspaceInvitationId) {
        WorkspaceInvitationResponse response =  invitationService.acceptWorkspaceInvite(workspaceInvitationId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectInvitationId}/projects/decline")
    public ResponseEntity<ProjectInvitationResponse> declineProjectInvite(@PathVariable String projectInvitationId) {
        ProjectInvitationResponse response = invitationService.declineProjectInvite(projectInvitationId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{workspaceInvitationId}/workspaces/decline")
    public ResponseEntity<WorkspaceInvitationResponse> declineWorkspaceInvite(@PathVariable String workspaceInvitationId) {
        WorkspaceInvitationResponse response = invitationService.declineWorkspaceInvite(workspaceInvitationId);
        return ResponseEntity.ok(response);
    }
}
