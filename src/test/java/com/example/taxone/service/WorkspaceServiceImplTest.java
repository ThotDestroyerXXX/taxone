package com.example.taxone.service;

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
import com.example.taxone.service.impl.WorkspaceServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceImplTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMapper workspaceMapper;

    @InjectMocks
    private WorkspaceServiceImpl workspaceService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getWorkspaces_success() {
        User user = mockUser();
        authenticate(user);

        Workspace ws1 = mockWorkspace(user);
        Workspace ws2 = mockWorkspace(user);

        when(workspaceRepository.findAllByUserId(user.getId()))
                .thenReturn(List.of(ws1, ws2));

        when(workspaceMapper.toResponseList(any()))
                .thenReturn(List.of(new WorkspaceResponse(), new WorkspaceResponse()));

        List<WorkspaceResponse> result = workspaceService.getWorkspaces();

        assertEquals(2, result.size());
        verify(workspaceRepository).findAllByUserId(user.getId());
    }

    @Test
    void getWorkspace_success() {
        User user = mockUser();
        authenticate(user);

        Workspace workspace = mockWorkspace(user);

        when(workspaceRepository.findByIdAndUserHasAccess(
                workspace.getId(), user.getId()))
                .thenReturn(java.util.Optional.of(workspace));

        when(workspaceMapper.toResponse(workspace))
                .thenReturn(new WorkspaceResponse());

        WorkspaceResponse response =
                workspaceService.getWorkspace(workspace.getId().toString());

        assertNotNull(response);
    }

    @Test
    void getWorkspace_notFound() {
        User user = mockUser();
        authenticate(user);

        UUID id = UUID.randomUUID();

        when(workspaceRepository.findByIdAndUserHasAccess(id, user.getId()))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> workspaceService.getWorkspace(id.toString()));
    }


    // =========================
    // SUCCESS CASE
    // =========================
    @Test
    void createWorkspace_success() {
        // Arrange: authenticated user
        User user = new User();
        user.setId(UUID.randomUUID());

        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        WorkspaceRequest request = new WorkspaceRequest();
        request.setName("My Workspace");
        request.setDescription("Desc");
        request.setSlug("my-workspace");

        Workspace workspace = Workspace.builder()
                .name("My Workspace")
                .slug("my-workspace")
                .owner(user)
                .build();

        WorkspaceResponse response = new WorkspaceResponse();
        response.setName("My Workspace");

        when(workspaceRepository.existsBySlug("my-workspace")).thenReturn(false);
        when(workspaceRepository.save(any(Workspace.class))).thenReturn(workspace);
        when(workspaceMapper.toResponse(any(Workspace.class))).thenReturn(response);

        // Act
        WorkspaceResponse result = workspaceService.createWorkspace(request);

        // Assert
        assertNotNull(result);
        assertEquals("My Workspace", result.getName());

        verify(workspaceRepository).save(any(Workspace.class));
        verify(workspaceMapper).toResponse(any(Workspace.class));
    }

    @Test
    void updateWorkspace_success() {
        User user = mockUser();
        authenticate(user);

        Workspace workspace = mockWorkspace(user);

        WorkspaceRequest request = new WorkspaceRequest();
        request.setName("Updated");
        request.setSlug("updated-slug");

        when(workspaceRepository.findById(workspace.getId()))
                .thenReturn(java.util.Optional.of(workspace));
        when(workspaceRepository.existsBySlugAndIdNot("updated-slug", workspace.getId()))
                .thenReturn(false);
        when(workspaceRepository.save(any()))
                .thenReturn(workspace);
        when(workspaceMapper.toResponse(any()))
                .thenReturn(new WorkspaceResponse());

        WorkspaceResponse response =
                workspaceService.updateWorkspace(workspace.getId().toString(), request);

        assertNotNull(response);
    }

    @Test
    void updateWorkspace_notOwner_throwForbidden() {
        User owner = mockUser();
        User otherUser = mockUser();
        authenticate(otherUser);

        Workspace workspace = mockWorkspace(owner);

        when(workspaceRepository.findById(workspace.getId()))
                .thenReturn(java.util.Optional.of(workspace));

        assertThrows(ForbiddenException.class,
                () -> workspaceService.updateWorkspace(
                        workspace.getId().toString(), new WorkspaceRequest()));
    }

    // =========================
    // SLUG NOT UNIQUE
    // =========================
    @Test
    void createWorkspace_slugAlreadyExists_throwException() {
        // Arrange: authenticated user
        User user = new User();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null)
        );

        WorkspaceRequest request = new WorkspaceRequest();
        request.setSlug("duplicate-slug");

        when(workspaceRepository.existsBySlug("duplicate-slug")).thenReturn(true);

        // Act + Assert
        BusinessValidationException exception =
                assertThrows(BusinessValidationException.class,
                        () -> workspaceService.createWorkspace(request));

        assertTrue(
                exception.getErrors()
                        .stream()
                        .anyMatch(error ->
                                "slug".equals(error.getField()) &&
                                        "Slug must be unique".equals(error.getMessage())
                        )
        );

        verify(workspaceRepository, never()).save(any());
        verify(workspaceMapper, never()).toResponse(any());
    }

    @Test
    void updateWorkspace_slugConflict() {
        User user = mockUser();
        authenticate(user);

        Workspace workspace = mockWorkspace(user);

        WorkspaceRequest request = new WorkspaceRequest();
        request.setSlug("duplicate");

        when(workspaceRepository.findById(workspace.getId()))
                .thenReturn(java.util.Optional.of(workspace));
        when(workspaceRepository.existsBySlugAndIdNot("duplicate", workspace.getId()))
                .thenReturn(true);

        assertThrows(BusinessValidationException.class,
                () -> workspaceService.updateWorkspace(
                        workspace.getId().toString(), request));
    }


    // =========================
    // USER NOT AUTHENTICATED
    // =========================
    @Test
    void createWorkspace_userNotAuthenticated_throwException() {
        // Arrange
        SecurityContextHolder.clearContext();

        WorkspaceRequest request = new WorkspaceRequest();
        request.setSlug("test");

        // Act + Assert
        IllegalStateException exception =
                assertThrows(IllegalStateException.class,
                        () -> workspaceService.createWorkspace(request));

        assertEquals("User not authenticated", exception.getMessage());

        verifyNoInteractions(workspaceRepository);
        verifyNoInteractions(workspaceMapper);
    }

    @Test
    void deleteWorkspace_success() {
        User user = mockUser();
        authenticate(user);

        Workspace workspace = mockWorkspace(user);

        when(workspaceRepository.findById(workspace.getId()))
                .thenReturn(java.util.Optional.of(workspace));

        workspaceService.deleteWorkspace(workspace.getId().toString());

        assertFalse(workspace.getIsActive());
        verify(workspaceRepository).save(workspace);
    }

    @Test
    void restoreWorkspace_success() {
        User user = mockUser();
        authenticate(user);

        Workspace workspace = mockWorkspace(user);
        workspace.setIsActive(false);

        when(workspaceRepository.findById(workspace.getId()))
                .thenReturn(java.util.Optional.of(workspace));
        when(workspaceMapper.toResponse(workspace))
                .thenReturn(new WorkspaceResponse());

        WorkspaceResponse response =
                workspaceService.restoreWorkspace(workspace.getId().toString());

        assertTrue(workspace.getIsActive());
        assertNotNull(response);
    }

    // helper method
    private void authenticate(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private User mockUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    private Workspace mockWorkspace(User owner) {
        return Workspace.builder()
                .id(UUID.randomUUID())
                .name("Workspace")
                .slug("workspace")
                .owner(owner)
                .isActive(true)
                .build();
    }

}
