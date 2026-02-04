package com.example.taxone.service;

import com.example.taxone.dto.request.WorkspaceRequest;
import com.example.taxone.dto.response.WorkspaceResponse;
import com.example.taxone.entity.User;
import com.example.taxone.entity.Workspace;
import com.example.taxone.exception.BusinessValidationException;
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
}
