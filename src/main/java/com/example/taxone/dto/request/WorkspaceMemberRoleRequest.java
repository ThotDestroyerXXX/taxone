package com.example.taxone.dto.request;


import com.example.taxone.entity.WorkspaceMember;
import com.example.taxone.validator.ValueOfEnum;
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
    @ValueOfEnum(enumClass = WorkspaceMember.MemberType.class, message = "role should be valid")
    private String  memberType;
}
