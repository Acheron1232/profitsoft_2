package com.acheron.profitsoft2.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthorSaveDto(
        @Size(max = 600, message = "Contact info must be shorter than 600 characters")
        String contactInfo,
        @NotBlank(message = "First name cannot be empty")
        @Size(min = 2, max = 255, message = "First name must be 2–255 characters long")
        String firstName,
        @NotBlank(message = "Last name cannot be empty")
        @Size(min = 2, max = 255, message = "Last name must be 2–255 characters long")
        String lastName
) {
}
