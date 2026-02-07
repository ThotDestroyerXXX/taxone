package com.example.taxone.dto.request;

import com.example.taxone.entity.ProjectMember;
import com.example.taxone.entity.WorkspaceMember;
import com.example.taxone.validator.ValueOfEnum;
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
    @ValueOfEnum(enumClass = ProjectMember.ProjectMemberType.class, message = "role should be valid")
    private String memberType;
}
