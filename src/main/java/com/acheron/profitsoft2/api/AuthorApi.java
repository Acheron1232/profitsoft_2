package com.acheron.profitsoft2.api;

import com.acheron.profitsoft2.dto.request.AuthorSaveDto;
import com.acheron.profitsoft2.dto.request.AuthorUpdateDto;
import com.acheron.profitsoft2.dto.response.AuthorDto;
import com.acheron.profitsoft2.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST API for managing Author entities with logging.
 */
@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
public class AuthorApi {

    private static final Logger log = LoggerFactory.getLogger(AuthorApi.class);

    private final AuthorService authorService;

    /**
     * Get all authors
     */
    @GetMapping
    public List<AuthorDto> getAuthors() {
        log.info("API call: Get all authors");
        List<AuthorDto> authors = authorService.findAll();
        log.info("Found {} authors", authors.size());
        return authors;
    }

    /**
     * Create a new author
     */
    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody @Valid AuthorSaveDto authorDto) {
        log.info("API call: Create author '{} {}'", authorDto.firstName(), authorDto.lastName());
        ResponseEntity<AuthorDto> response = authorService.save(authorDto);
        log.info("Author created with Name: {}", response.getBody().firstName() + " " + response.getBody().lastName());
        return response;
    }

    /**
     * Update an existing author by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable UUID id,
                                                  @RequestBody @Valid AuthorUpdateDto authorDto) {
        log.info("API call: Update author ID {}", id);
        ResponseEntity<AuthorDto> response = authorService.update(id, authorDto);
        log.info("Author updated: {} {}", response.getBody().firstName(), response.getBody().lastName());
        return response;
    }

    /**
     * Delete an author by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable UUID id) {
        log.info("API call: Delete author ID {}", id);
        authorService.delete(id);
        log.info("Author deleted ID {}", id);
        return ResponseEntity.ok(id.toString());
    }
}
