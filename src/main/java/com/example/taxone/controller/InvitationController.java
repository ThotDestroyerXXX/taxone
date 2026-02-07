package com.example.taxone.controller;


import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/projects/{projectInvitationId}/accept")
    public ResponseEntity<ProjectInvitationResponse> acceptProjectInvite(@PathVariable String projectInvitationId) {
        ProjectInvitationResponse response = invitationService.acceptProjectInvite(projectInvitationId);
        return ResponseEntity.ok(response);
    }
}
