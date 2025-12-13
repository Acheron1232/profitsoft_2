package com.acheron.profitsoft2.dto.request;

import java.time.Instant;

public record UploadBookDto(
        String title,
        String isbn,
        Instant publishDate,
        String authorId
) {
}
