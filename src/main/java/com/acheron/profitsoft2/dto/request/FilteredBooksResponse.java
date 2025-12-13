package com.acheron.profitsoft2.dto.request;

import com.acheron.profitsoft2.dto.response.BookDto;

import java.util.List;

public record FilteredBooksResponse(
        List<BookDto> books,
        int totalPages
) {
}