package com.example.taxone.service.impl;

import com.example.taxone.dto.response.ProjectInvitationResponse;
import com.example.taxone.entity.InvitationStatus;
import com.example.taxone.entity.ProjectInvitation;
import com.example.taxone.entity.ProjectMember;
import com.example.taxone.entity.User;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.ProjectInvitationMapper;
import com.example.taxone.repository.ProjectInvitationRepository;
import com.example.taxone.repository.ProjectMemberRepository;
import com.example.taxone.repository.WorkspaceInvitationRepository;
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
    private final ProjectInvitationRepository projectInvitationRepository;
    private final ProjectMemberRepository  projectMemberRepository;

    private final ProjectInvitationMapper projectInvitationMapper;


    @Override
    @Transactional
    public ProjectInvitationResponse acceptProjectInvite(String projectInvitationId) {
        User user = getCurrentUser();
        UUID projectInvitationUUID = UUIDUtils.fromString(projectInvitationId, "project");

        ProjectInvitation existingProjectInvitation = projectInvitationRepository
                .findByIdAndEmail(projectInvitationUUID, user.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("invitation not found"));

        ensureIsPendingInvitationStatus(existingProjectInvitation.getStatus());

        existingProjectInvitation.setStatus(InvitationStatus.ACCEPTED);

        // change all other status to expired
        projectInvitationRepository.updateStatusByEmailAndStatus(
                user.getEmail(),
                InvitationStatus.PENDING,
                InvitationStatus.EXPIRED
        );

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
