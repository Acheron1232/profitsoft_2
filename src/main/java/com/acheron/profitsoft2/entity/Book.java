package com.acheron.profitsoft2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "authorId", nullable = false)
    private Author author;


    @NotBlank(message = "Title cannot be empty")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    /**
     * ISBN stands for International Standard Book Number,
     * a unique 10 or 13-digit identifier assigned to each specific edition of a book.
     */
    @NotBlank(message = "ISBN cannot be empty")
    @Pattern(
            regexp = "^(\\d{10}|\\d{13})$",
            message = "ISBN must be 10 or 13 digits long"
    )
    private String isbn;

    @Column(name = "publish_date")
    private Instant publishDate;
}
