package com.acheron.profitsoft2.service;

import com.acheron.profitsoft2.dto.request.AuthorSaveDto;
import com.acheron.profitsoft2.dto.request.AuthorUpdateDto;
import com.acheron.profitsoft2.dto.response.AuthorDto;
import com.acheron.profitsoft2.entity.Author;
import com.acheron.profitsoft2.mapper.AuthorMapper;
import com.acheron.profitsoft2.repository.AuthorRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for handling Author entity operations with logging.
 */
@Service
public class AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorService.class);

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorService(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    /**
     * Get all authors.
     *
     * @return list of AuthorDto
     */
    public List<AuthorDto> findAll() {
        log.info("Fetching all authors");
        List<AuthorDto> authors = authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .toList();
        log.info("Found {} authors", authors.size());
        return authors;
    }

    /**
     * Save a new author.
     *
     * @param dto AuthorSaveDto
     * @return saved AuthorDto
     */
    public ResponseEntity<AuthorDto> save(@Valid AuthorSaveDto dto) {
        log.info("Saving new author: {} {}", dto.firstName(), dto.lastName());
        Author author = authorMapper.toEntity(dto);
        Author saved = authorRepository.save(author);
        log.info("Saved author with ID: {}", saved.getId());
        return ResponseEntity.ok(authorMapper.toDto(saved));
    }

    /**
     * Update existing author by ID.
     *
     * @param id  Author UUID
     * @param dto AuthorUpdateDto
     * @return updated AuthorDto
     */
    public ResponseEntity<AuthorDto> update(UUID id, @Valid AuthorUpdateDto dto) {
        log.info("Updating author with ID: {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Author not found: {}", id);
                    return new RuntimeException("Author not found: " + id);
                });

        if (dto.firstName() != null) author.setFirstName(dto.firstName());
        if (dto.lastName() != null) author.setLastName(dto.lastName());
        if (dto.contactInfo() != null) author.setContactInfo(dto.contactInfo());

        Author saved = authorRepository.save(author);
        log.info("Updated author with ID: {}", saved.getId());
        return ResponseEntity.ok(authorMapper.toDto(saved));
    }

    /**
     * Delete author by ID.
     *
     * @param id Author UUID
     */
    public void delete(UUID id) {
        log.info("Deleting author with ID: {}", id);
        if (!authorRepository.existsById(id)) {
            log.warn("Author not found: {}", id);
            throw new RuntimeException("Author not found: " + id);
        }
        authorRepository.deleteById(id);
        log.info("Deleted author with ID: {}", id);
    }
}
