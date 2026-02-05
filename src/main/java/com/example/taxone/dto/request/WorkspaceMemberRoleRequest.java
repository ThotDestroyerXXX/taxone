package com.example.taxone.dto.request;


import com.example.taxone.entity.WorkspaceMember;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkspaceMemberRoleRequest {
    @NotBlank(message = "role is required")
    private WorkspaceMember.MemberType  memberType;
}
