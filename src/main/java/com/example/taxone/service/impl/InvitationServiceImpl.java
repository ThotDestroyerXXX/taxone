package com.example.taxone.service.impl;

import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.dto.response.WorkspaceInvitationResponse;
import com.example.taxone.entity.*;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.ProjectInvitationMapper;
import com.example.taxone.mapper.WorkspaceInvitationMapper;
import com.example.taxone.repository.ProjectInvitationRepository;
import com.example.taxone.repository.ProjectMemberRepository;
import com.example.taxone.repository.WorkspaceInvitationRepository;
import com.example.taxone.repository.WorkspaceMemberRepository;
import com.example.taxone.security.CustomUserDetails;
import com.example.taxone.service.InvitationService;
import com.example.taxone.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final WorkspaceInvitationRepository workspaceInvitationRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ProjectInvitationRepository projectInvitationRepository;
    private final ProjectMemberRepository  projectMemberRepository;

    private final ProjectInvitationMapper projectInvitationMapper;
    private final WorkspaceInvitationMapper workspaceInvitationMapper;


    @Override
    @Transactional
    public ProjectInvitationResponse acceptProjectInvite(String projectInvitationId) {
        User user = getCurrentUser();
        UUID projectInvitationUUID = UUIDUtils.fromString(projectInvitationId, "project invitation");

        ProjectInvitation existingProjectInvitation = projectInvitationRepository
                .findByIdAndEmail(projectInvitationUUID, user.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("invitation not found"));

        ensureIsPendingInvitationStatus(existingProjectInvitation.getStatus());

        // change all other status to expired
        projectInvitationRepository.updateStatusByEmailAndStatus(
                user.getEmail(),
                InvitationStatus.PENDING,
                InvitationStatus.EXPIRED
        );

        existingProjectInvitation.setStatus(InvitationStatus.ACCEPTED);

        // add user to project member
        ProjectMember newMember = ProjectMember
                .builder()
                .project(existingProjectInvitation.getProject())
                .memberType(existingProjectInvitation.getMemberType())
                .addedBy(existingProjectInvitation.getInvitedBy())
                .user(user)
                .build();

        projectInvitationRepository.save(existingProjectInvitation);
        projectMemberRepository.save(newMember);

        return projectInvitationMapper.toResponse(existingProjectInvitation);
    }

    @Override
    @Transactional
    public WorkspaceInvitationResponse acceptWorkspaceInvite(String workspaceInvitationId) {
        User user = getCurrentUser();
        UUID workspaceInvitationUUID = UUIDUtils.fromString(workspaceInvitationId, "workspace invitation");

        WorkspaceInvitation existingWorkspaceInvitation = workspaceInvitationRepository
                .findByIdAndEmail(workspaceInvitationUUID, user.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("invitation not found"));

        ensureIsPendingInvitationStatus(existingWorkspaceInvitation.getStatus());

        // change all other status to expired
        workspaceInvitationRepository.updateStatusByEmailAndStatus(
                user.getEmail(),
                InvitationStatus.PENDING,
                InvitationStatus.EXPIRED
        );

        existingWorkspaceInvitation.setStatus(InvitationStatus.ACCEPTED);

        // add user to project member
        WorkspaceMember newMember = WorkspaceMember
                .builder()
                .workspace(existingWorkspaceInvitation.getWorkspace())
                .memberType(existingWorkspaceInvitation.getMemberType())
                .invitedBy(existingWorkspaceInvitation.getInvitedBy())
                .user(user)
                .build();

        workspaceInvitationRepository.save(existingWorkspaceInvitation);
        workspaceMemberRepository.save(newMember);

        return workspaceInvitationMapper.toResponse(existingWorkspaceInvitation);
    }

    @Override
    public ProjectInvitationResponse declineProjectInvite(String projectInvitationId) {
        User user = getCurrentUser();
        UUID projectInvitationUUID = UUIDUtils.fromString(projectInvitationId, "project invitation");

        // ensure that the invitation exists
        ProjectInvitation existingProjectInvitation = projectInvitationRepository
                .findByIdAndEmail(projectInvitationUUID, user.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("invitation not found"));

        ensureIsPendingInvitationStatus(existingProjectInvitation.getStatus());

        existingProjectInvitation.setStatus(InvitationStatus.DECLINED);
        projectInvitationRepository.save(existingProjectInvitation);

        return projectInvitationMapper.toResponse(existingProjectInvitation);
    }

    @Override
    public WorkspaceInvitationResponse declineWorkspaceInvite(String workspaceInvitationId) {
        User user = getCurrentUser();
        UUID workspaceInvitationUUID = UUIDUtils.fromString(workspaceInvitationId, "workspace invitation");

        WorkspaceInvitation existingWorkspaceInvitation = workspaceInvitationRepository
                .findByIdAndEmail(workspaceInvitationUUID, user.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("invitation not found"));

        ensureIsPendingInvitationStatus(existingWorkspaceInvitation.getStatus());
        existingWorkspaceInvitation.setStatus(InvitationStatus.DECLINED);

        workspaceInvitationRepository.save(existingWorkspaceInvitation);

        return workspaceInvitationMapper.toResponse(existingWorkspaceInvitation);
    }

    // helper methods
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        throw new IllegalStateException("User not authenticated");
    }

    private void ensureIsPendingInvitationStatus(InvitationStatus status) {
        if(!status.equals(InvitationStatus.PENDING)) {
            throw new IllegalStateException("Invitation is not pending");
        }
    }
}
