package com.example.taxone.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkspaceRequest {

    @NotBlank(message = "name is required!")
    @Size(min = 2, max = 200, message = "name must be between 2 and 200 characters!")
    private String name;

    @NotBlank(message = "description is required!")
    private String description;

    @NotBlank(message = "slug is required!")
    @Size(min = 2, max = 200, message = "slug must be between 2 and 200 characters!")
    private String slug;

    @Size(min = 2, max = 200, message = "logo url must be between 2 and 200 characters!")
    private String logoUrl;

}
