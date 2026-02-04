package com.example.taxone.service.impl;

import com.example.taxone.dto.request.WorkspaceRequest;
import com.example.taxone.dto.response.WorkspaceResponse;
import com.example.taxone.entity.User;
import com.example.taxone.entity.Workspace;
import com.example.taxone.exception.BusinessValidationException;
import com.example.taxone.exception.ForbiddenException;
import com.example.taxone.exception.ResourceNotFoundException;
import com.example.taxone.mapper.WorkspaceMapper;
import com.example.taxone.repository.WorkspaceRepository;
import com.example.taxone.security.CustomUserDetails;
import com.example.taxone.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;

    @Override
    public WorkspaceResponse createWorkspace(WorkspaceRequest workspaceRequest) {
        User user = getCurrentUser();

        if(!isUniqueSlug(workspaceRequest.getSlug())) {
            throw new BusinessValidationException("slug", "Slug must be unique");
        }

        Workspace newWorkspace = Workspace
                .builder()
                .name(workspaceRequest.getName())
                .description(workspaceRequest.getDescription())
                .logoUrl(workspaceRequest.getLogoUrl())
                .owner(user)
                .slug(workspaceRequest.getSlug())
                .build();
        workspaceRepository.save(newWorkspace);

        return workspaceMapper.toResponse(newWorkspace);

    }

    @Override
    public List<WorkspaceResponse> getWorkspaces() {
        UUID userId = getCurrentUser().getId();

        List<Workspace> workspaces =
                workspaceRepository.findAllByUserId(userId);

        return workspaceMapper.toResponseList(workspaces);
    }

    @Override
    public WorkspaceResponse getWorkspace(String id) {
        User user = getCurrentUser();

        UUID workspaceId;
        try {
            workspaceId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new BusinessValidationException("id", "Invalid workspace id");
        }

        Workspace workspace = workspaceRepository
                .findByIdAndUserHasAccess(workspaceId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        return workspaceMapper.toResponse(workspace);
    }

    @Override
    public WorkspaceResponse updateWorkspace(String id, WorkspaceRequest request) {
        User user = getCurrentUser();

        UUID workspaceId;
        try {
            workspaceId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new BusinessValidationException("id", "Invalid workspace id");
        }

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Workspace not found"));

        if (!isOwnerOfWorkspace(user, workspace)) {
            throw new ForbiddenException("You are not allowed to update this workspace");
        }

        if (!isSlugUniqueForUpdate(request.getSlug(), workspace.getId())) {
            throw new BusinessValidationException("slug", "Slug must be unique");
        }

        // Update existing entity (IMPORTANT)
        workspace.setName(request.getName());
        workspace.setDescription(request.getDescription());
        workspace.setLogoUrl(request.getLogoUrl());
        workspace.setSlug(request.getSlug());

        Workspace saved = workspaceRepository.save(workspace);

        return workspaceMapper.toResponse(saved);
    }

    // helper methods
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }

        throw new IllegalStateException("User not authenticated");
    }

    private Boolean isUniqueSlug(String slug) {
        return !workspaceRepository.existsBySlug(slug);
    }

    private Boolean isOwnerOfWorkspace(User user, Workspace workspace) {
        return workspace.getOwner().getId().equals(user.getId());
    }

    private boolean isSlugUniqueForUpdate(String slug, UUID workspaceId) {
        return !workspaceRepository.existsBySlugAndIdNot(slug, workspaceId);
    }
}
