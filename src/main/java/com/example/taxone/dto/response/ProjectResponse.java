package com.example.taxone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponse {
    private UUID id;
    private UserResponse owner;
    private String name;
    private String description;
    private String projectKey;
    private String status;
    private String priority;
    private String color;
    private String startDate;
    private String endDate;
    private Boolean isPublic;
}
