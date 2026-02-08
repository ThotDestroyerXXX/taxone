package com.example.taxone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelRequest {
    @NotBlank(message = "name is required")
    @Size(min = 2, max = 100, message = "name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "color is required")
    @Pattern(
            regexp = "^#[0-9A-Fa-f]{6}$",
            message = "color must be a valid hex color (e.g. #A1B2C3)"
    )
    private String color;

    @NotBlank(message = "description is required")
    private String description;
}
