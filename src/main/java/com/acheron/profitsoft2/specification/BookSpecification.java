package com.acheron.profitsoft2.specification;

import com.acheron.profitsoft2.entity.Book;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for dynamic Book specifications with logging.
 */
@UtilityClass
public class BookSpecification {

    private static final Logger log = LoggerFactory.getLogger(BookSpecification.class);

    public Specification<Book> publishedAfter(Date date) {
        Instant instant = date.toInstant();
        log.info("Creating specification: publishedAfter {}", date);
        return (root, query, cb) -> cb.greaterThan(root.get("publishDate"), instant);
    }

    public Specification<Book> publishedBefore(Date date) {
        Instant instant = date.toInstant();
        log.info("Creating specification: publishedBefore {}", date);
        return (root, query, cb) -> cb.lessThan(root.get("publishDate"), instant);
    }

    public Specification<Book> publishedBetween(Date from, Date to) {
        Instant start = from.toInstant();
        Instant end = to.toInstant();
        log.info("Creating specification: publishedBetween {} and {}", from, to);
        return (root, query, cb) -> cb.between(root.get("publishDate"), start, end);
    }

    public Specification<Book> publishedOn(Date date) {
        Instant start = date.toInstant().truncatedTo(ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.DAYS);
        log.info("Creating specification: publishedOn {}", date);
        return (root, query, cb) -> cb.between(root.get("publishDate"), start, end);
    }

    public Specification<Book> publishedBy(UUID authorId) {
        log.info("Creating specification: publishedBy authorId={}", authorId);
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public Specification<Book> titleContains(String queryStr) {
        log.info("Creating specification: titleContains '{}'", queryStr);
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + queryStr.toLowerCase() + "%");
    }

    public Specification<Book> hasIsbn(String isbn) {
        log.info("Creating specification: hasIsbn '{}'", isbn);
        return (root, query, cb) -> cb.equal(root.get("isbn"), isbn);
    }
}
