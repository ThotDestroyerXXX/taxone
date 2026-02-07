package com.example.taxone.dto.request;

import com.example.taxone.dto.response.UserResponse;
import com.example.taxone.entity.Project;
import com.example.taxone.validator.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectRequest {

    @NotBlank(message = "name is required")
    @Size(min = 2, max = 100, message = "name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "project key is required")
    @Size(min = 3, max = 20, message = "project key must be between 3 and 20 characters")
    private String projectKey;

    @ValueOfEnum(enumClass = Project.ProjectPriority.class, message = "Priority should be valid")
    private String priority;

    @NotBlank(message = "color is required")
    @Pattern(
            regexp = "^#[0-9A-Fa-f]{6}$",
            message = "color must be a valid hex color (e.g. #A1B2C3)"
    )
    private String color;

    @NotNull(message = "start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDate;

    @NotBlank(message = "end date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String endDate;

    @NotNull(message = "publicity is required")
    private Boolean isPublic;
}
