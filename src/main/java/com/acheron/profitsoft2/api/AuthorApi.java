package com.acheron.profitsoft2.api;

import com.acheron.profitsoft2.dto.response.AuthorDto;
import com.acheron.profitsoft2.dto.request.AuthorSaveDto;
import com.acheron.profitsoft2.dto.request.AuthorUpdateDto;
import com.acheron.profitsoft2.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST API for managing authors.
 */
@RestController
@RequestMapping("/api/author")
@RequiredArgsConstructor
public class AuthorApi {

    private final AuthorService authorService;

    @GetMapping
    public List<AuthorDto> getAuthors() {
        return authorService.findAll();
    }

    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody @Valid AuthorSaveDto authorDto) {
        return authorService.save(authorDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable UUID id,
                                                  @RequestBody @Valid AuthorUpdateDto authorDto) {
        return authorService.update(id, authorDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuthor(@PathVariable UUID id) {
        authorService.delete(id);
        return ResponseEntity.ok(id.toString());
    }
}
