package com.example.taxone.dto.request;

import com.example.taxone.entity.ProjectMember;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberRoleRequest {
    @NotBlank(message = "role is required")
    private ProjectMember.ProjectMemberType memberType;
}
