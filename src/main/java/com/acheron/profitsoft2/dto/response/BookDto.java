package com.acheron.profitsoft2.dto.response;

import java.time.Instant;

public record BookDto(
        AuthorDto author,
        String title,
        String isbn,
        Instant publishDate
) {
}
