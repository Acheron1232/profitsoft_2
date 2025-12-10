package com.acheron.profitsoft2.dto.request;

import com.acheron.profitsoft2.entity.Book;
import com.acheron.profitsoft2.specification.BookSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.UUID;


public record ListBookRequest(
        String title,
        PublishedDate publishedDate,
        String isbn,
        String author_id,
        Integer page,
        Integer size
) implements Filterable<Book> {

    @Override
    public Specification<Book> toSpecification() {
        Specification<Book> specification = Specification.allOf();


        if (title != null && !title.isBlank()) {
            specification = specification.and(BookSpecification.titleContains(title));
        }


        if (isbn != null && !isbn.isBlank()) {
            specification = specification.and(BookSpecification.hasIsbn(isbn));
        }


        if (author_id != null && !author_id.isBlank()) {
            try {
                UUID authorId = UUID.fromString(author_id);
                specification = specification.and(BookSpecification.publishedBy(authorId));
            } catch (IllegalArgumentException ignored) {}
        }


        if (publishedDate != null) {

            if (publishedDate.publishDate() != null) {
                specification = specification.and(
                        BookSpecification.publishedOn(publishedDate.publishDate())
                );
            }

            if (publishedDate.publishedAfter() != null) {
                specification = specification.and(
                        BookSpecification.publishedAfter(publishedDate.publishedAfter())
                );
            }

            if (publishedDate.publishedBefore() != null) {
                specification = specification.and(
                        BookSpecification.publishedBefore(publishedDate.publishedBefore())
                );
            }

            if (publishedDate.publishedAfter() != null &&
                    publishedDate.publishedBefore() != null) {

                specification = specification.and(
                        BookSpecification.publishedBetween(
                                publishedDate.publishedAfter(),
                                publishedDate.publishedBefore()
                        )
                );
            }
        }

        return specification;
    }


    public record PublishedDate(
            Date publishDate,
            Date publishedBefore,
            Date publishedAfter
    ) {}
}

