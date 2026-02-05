package com.example.taxone.dto.response;


import com.example.taxone.entity.WorkspaceMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceMemberResponse {
    private UUID id;
    private UserResponse user;
    private WorkspaceMember.MemberType memberType;
    private LocalDateTime joinedAt;
}
