package com.acheron.profitsoft2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "author", orphanRemoval = true)
    private List<Book> books;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name cannot be empty")
    @Size(min = 2, max = 255, message = "First name must be 2–255 characters long")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 2, max = 255, message = "Last name must be 2–255 characters long")
    private String lastName;

    @Column(name = "contact_info", nullable = false)
    @Size(max = 600, message = "Contact info must be shorter than 600 characters")
    @NotBlank
    private String contactInfo;
}
