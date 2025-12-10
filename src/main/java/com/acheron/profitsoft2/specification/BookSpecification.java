package com.acheron.profitsoft2.specification;

import com.acheron.profitsoft2.entity.Book;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class BookSpecification {

    public Specification<Book> publishedAfter(Date date) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("publishDate"), date.toInstant());
    }

    public Specification<Book> publishedBefore(Date date) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.lessThan(root.get("publishDate"), date.toInstant());
    }

    public Specification<Book> publishedBy(UUID id) {
        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("author").get("id"), id);
    }

    public static Specification<Book> publishedBetween(Date from, Date to) {
        Instant start = from.toInstant();
        Instant end = to.toInstant();
        return (root, q, cb) ->
                cb.between(root.get("publishDate"), start, end);
    }


    public Specification<Book> publishedOn(Date date) {
        Instant start = date.toInstant().truncatedTo(ChronoUnit.DAYS);
        Instant end = start.plus(1, ChronoUnit.DAYS);

        return (root, query, cb) ->
                cb.between(root.get("publishDate"), start, end);
    }

    public static Specification<Book> titleContains(String query) {
        return (root, q, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + query.toLowerCase() + "%");
    }

    public static Specification<Book> hasIsbn(String isbn) {
        return (root, q, cb) ->
                cb.equal(root.get("isbn"), isbn);
    }
}