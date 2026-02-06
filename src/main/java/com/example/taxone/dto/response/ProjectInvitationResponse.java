package com.example.taxone.dto.response;


import com.example.taxone.entity.InvitationStatus;
import com.example.taxone.entity.ProjectMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInvitationResponse {
    private UUID id;
    private String email;
    private UserResponse invitedBy;
    private ProjectMember.ProjectMemberType memberType;
    private InvitationStatus status;
}
