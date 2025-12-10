package com.acheron.profitsoft2.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookUpdateDto(


        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        @Pattern(
                regexp = "^(\\d{10}|\\d{13})$",
                message = "ISBN must be 10 or 13 digits long"
        )
        String isbn
) {
}
