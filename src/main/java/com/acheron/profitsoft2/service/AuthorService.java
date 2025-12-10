package com.acheron.profitsoft2.service;

import com.acheron.profitsoft2.dto.response.AuthorDto;
import com.acheron.profitsoft2.dto.request.AuthorSaveDto;
import com.acheron.profitsoft2.dto.request.AuthorUpdateDto;
import com.acheron.profitsoft2.entity.Author;
import com.acheron.profitsoft2.mapper.AuthorMapper;
import com.acheron.profitsoft2.repository.AuthorRepository;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthorService extends EntityService<Author, UUID> {
    private final AuthorMapper authorMapper;

    public AuthorService(JpaRepository<Author, UUID> repository,AuthorMapper authorMapper) {
        super(repository);
        this.authorMapper = authorMapper;
    }

    public List<Author> findAll() {
        AuthorRepository repo = (AuthorRepository) repository;
        return repo.findAll();
    }

    public Optional<Author> findOptionalById(UUID id){
        return repository.findById(id);
    }

    public ResponseEntity<AuthorDto> save(@Valid AuthorSaveDto dto){
        Author save = save(authorMapper.map(dto));
        return ResponseEntity.ok(authorMapper.map(save));
    }

    public ResponseEntity<AuthorDto> update(UUID id, AuthorUpdateDto dto) {

        AuthorRepository authorRepository = (AuthorRepository) repository;

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found: " + id));

        author.setFirstName(dto.firstName()==null? author.getFirstName():dto.firstName());
        author.setLastName(dto.lastName()==null? author.getLastName():dto.lastName());
        author.setContactInfo(dto.contactInfo()==null? author.getContactInfo():dto.contactInfo());

        Author saved = authorRepository.save(author);
        AuthorDto response = authorMapper.map(saved);

        return ResponseEntity.ok(response);
    }

    public void delete(UUID id) {
        AuthorRepository authorRepository = (AuthorRepository) repository;
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found: " + id);
        }
        authorRepository.deleteById(id);
    }


}
