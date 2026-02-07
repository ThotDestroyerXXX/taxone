package com.example.taxone.controller;

import com.example.taxone.dto.request.ProjectInvitationRequest;
import com.example.taxone.dto.request.ProjectMemberRoleRequest;
import com.example.taxone.dto.request.ProjectRequest;
import com.example.taxone.dto.request.TaskRequest;
import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.dto.response.ProjectMemberResponse;
import com.example.taxone.dto.response.ProjectResponse;
import com.example.taxone.dto.response.TaskResponse;
import com.example.taxone.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable String projectId) {
        ProjectResponse response = projectService.getProject(projectId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable String projectId,
                                                         @RequestBody @Valid ProjectRequest projectRequest) {
        ProjectResponse response = projectService.updateProject(projectId, projectRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/archive")
    public ResponseEntity<ProjectResponse> archiveProject(@PathVariable String projectId) {
        ProjectResponse response = projectService.archiveProject(projectId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/hold")
    public ResponseEntity<ProjectResponse> holdProject(@PathVariable String projectId) {
        ProjectResponse response = projectService.holdProject(projectId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/restore")
    public ResponseEntity<ProjectResponse> restoreProject(@PathVariable String projectId) {
        ProjectResponse response = projectService.restoreProject(projectId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<List<ProjectMemberResponse>> getMembers(@PathVariable String projectId) {
        List<ProjectMemberResponse> response = projectService.getMembers(projectId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<ProjectInvitationResponse> inviteMember(
            @PathVariable String projectId,
            @RequestBody @Valid ProjectInvitationRequest invitationRequest) {
        ProjectInvitationResponse response = projectService.inviteMember(projectId, invitationRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<ProjectMemberResponse> updateMemberRole(@PathVariable String projectId,
                                                                  @PathVariable String memberId,
                                                                  @RequestBody @Valid ProjectMemberRoleRequest roleRequest) {
        ProjectMemberResponse response = projectService.updateMemberRole(projectId, memberId, roleRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable String projectId, @PathVariable String memberId) {
        projectService.deleteMember(projectId, memberId);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/{projectId}/tasks")
//    public ResponseEntity<TaskResponse> createTask(@PathVariable String projectId,
//                                                   @RequestBody TaskRequest taskRequest) {
//
//    }
}
