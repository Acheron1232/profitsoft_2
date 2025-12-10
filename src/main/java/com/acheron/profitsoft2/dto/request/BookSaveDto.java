package com.acheron.profitsoft2.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookSaveDto(

        @JsonProperty("authorId")
        @Pattern(
                regexp = "^[0-9a-fA-F\\-]{36}$",
                message = "Invalid UUID format"
        )
        @NotBlank
        String authorId,

        @NotBlank(message = "Title cannot be empty")
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        @NotBlank(message = "ISBN cannot be empty")
        @Pattern(
                regexp = "^(\\d{10}|\\d{13})$",
                message = "ISBN must be 10 or 13 digits long"
        )
        String isbn

) {
}
