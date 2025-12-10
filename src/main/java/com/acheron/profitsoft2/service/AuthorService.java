package com.acheron.profitsoft2.service;

import com.acheron.profitsoft2.dto.response.AuthorDto;
import com.acheron.profitsoft2.dto.request.AuthorSaveDto;
import com.acheron.profitsoft2.dto.request.AuthorUpdateDto;
import com.acheron.profitsoft2.entity.Author;
import com.acheron.profitsoft2.mapper.AuthorMapper;
import com.acheron.profitsoft2.repository.AuthorRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for handling Author entity operations.
 */
@Service
public class AuthorService {

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
        return authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .toList();
    }

    /**
     * Save a new author.
     *
     * @param dto AuthorSaveDto
     * @return saved AuthorDto
     */
    public ResponseEntity<AuthorDto> save(@Valid AuthorSaveDto dto) {
        Author author = authorMapper.toEntity(dto);
        Author saved = authorRepository.save(author);
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
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found: " + id));

        if (dto.firstName() != null) author.setFirstName(dto.firstName());
        if (dto.lastName() != null) author.setLastName(dto.lastName());
        if (dto.contactInfo() != null) author.setContactInfo(dto.contactInfo());

        Author saved = authorRepository.save(author);
        return ResponseEntity.ok(authorMapper.toDto(saved));
    }

    /**
     * Delete author by ID.
     *
     * @param id Author UUID
     */
    public void delete(UUID id) {
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found: " + id);
        }
        authorRepository.deleteById(id);
    }
}
