package com.acheron.profitsoft2.specification;

import com.acheron.profitsoft2.entity.Book;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for dynamic Book specifications.
 */
@UtilityClass
public class BookSpecification {

    public Specification<Book> publishedAfter(Date date) {
        Instant instant = date.toInstant();
        return (root, query, cb) -> cb.greaterThan(root.get("publishDate"), instant);
    }

    public Specification<Book> publishedBefore(Date date) {
        Instant instant = date.toInstant();
        return (root, query, cb) -> cb.lessThan(root.get("publishDate"), instant);
    }

    public Specification<Book> publishedBetween(Date from, Date to) {
        Instant start = from.toInstant();
        Instant end = to.toInstant();
        return (root, query, cb) -> cb.between(root.get("publishDate"), start, end);
    }

    public Specification<Book> publishedOn(Date date) {
        Instant start = date.toInstant().truncatedTo(ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.DAYS);
        return (root, query, cb) -> cb.between(root.get("publishDate"), start, end);
    }

    public Specification<Book> publishedBy(UUID authorId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public Specification<Book> titleContains(String queryStr) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + queryStr.toLowerCase() + "%");
    }

    public Specification<Book> hasIsbn(String isbn) {
        return (root, query, cb) -> cb.equal(root.get("isbn"), isbn);
    }
}
