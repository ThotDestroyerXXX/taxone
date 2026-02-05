package com.example.taxone.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "first name is required")
    @Size(min = 2, max = 50, message = "first name must be between 2 and 50 characters!")
    private String firstName;

    @NotBlank(message = "first name is required")
    @Size(min = 2, max = 50, message = "first name must be between 2 and 50 characters!")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;
}
