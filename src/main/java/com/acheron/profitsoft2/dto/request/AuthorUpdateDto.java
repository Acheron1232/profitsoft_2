package com.acheron.profitsoft2.dto.request;

import jakarta.validation.constraints.Size;

public record AuthorUpdateDto(
        @Size(max = 600, message = "Contact info must be shorter than 600 characters")
        String contactInfo,
        @Size(min = 2, max = 255, message = "First name must be 2–255 characters long")
        String firstName,
        @Size(min = 2, max = 255, message = "Last name must be 2–255 characters long")
        String lastName
) {
}
