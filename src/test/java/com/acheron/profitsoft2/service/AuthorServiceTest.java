package com.acheron.profitsoft2.service;

import com.acheron.profitsoft2.dto.request.AuthorSaveDto;
import com.acheron.profitsoft2.dto.request.AuthorUpdateDto;
import com.acheron.profitsoft2.dto.response.AuthorDto;
import com.acheron.profitsoft2.entity.Author;
import com.acheron.profitsoft2.mapper.AuthorMapper;
import com.acheron.profitsoft2.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceTest {

    private AuthorRepository authorRepository;
    private AuthorMapper authorMapper;
    private AuthorService authorService;

    @BeforeEach
    void setup() {
        authorRepository = mock(AuthorRepository.class);
        authorMapper = mock(AuthorMapper.class);
        authorService = new AuthorService(authorRepository, authorMapper);
    }

    @Test
    void findAll_shouldReturnList() {
        Author author = new Author();
        AuthorDto dto = mock(AuthorDto.class);

        when(authorRepository.findAll()).thenReturn(List.of(author));
        when(authorMapper.toDto(author)).thenReturn(dto);

        List<AuthorDto> result = authorService.findAll();
        assertEquals(1, result.size());
        assertSame(dto, result.get(0));
    }

    @Test
    void save_shouldReturnSavedDto() {
        AuthorSaveDto dto = new AuthorSaveDto("contact", "John", "Doe");
        Author entity = new Author();
        Author savedEntity = new Author();
        AuthorDto savedDto = mock(AuthorDto.class);

        when(authorMapper.toEntity(dto)).thenReturn(entity);
        when(authorRepository.save(entity)).thenReturn(savedEntity);
        when(authorMapper.toDto(savedEntity)).thenReturn(savedDto);

        ResponseEntity<AuthorDto> result = authorService.save(dto);
        assertEquals(savedDto, result.getBody());
        verify(authorRepository).save(entity);
    }

    @Test
    void update_existingAuthor_shouldUpdateFields() {
        UUID id = UUID.randomUUID();
        Author author = new Author();
        AuthorUpdateDto dto = new AuthorUpdateDto("newContact", "Jane", "Smith");
        AuthorDto savedDto = mock(AuthorDto.class);

        when(authorRepository.findById(id)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(savedDto);

        ResponseEntity<AuthorDto> result = authorService.update(id, dto);

        assertEquals("Jane", author.getFirstName());
        assertEquals("Smith", author.getLastName());
        assertEquals("newContact", author.getContactInfo());
        assertEquals(savedDto, result.getBody());
    }

    @Test
    void update_nonExistingAuthor_shouldThrowException() {
        UUID id = UUID.randomUUID();
        AuthorUpdateDto dto = new AuthorUpdateDto("contact", "Jane", "Smith");

        when(authorRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authorService.update(id, dto));
    }

    @Test
    void delete_existingAuthor_shouldCallRepository() {
        UUID id = UUID.randomUUID();
        when(authorRepository.existsById(id)).thenReturn(true);

        authorService.delete(id);

        verify(authorRepository).deleteById(id);
    }

    @Test
    void delete_nonExistingAuthor_shouldThrowException() {
        UUID id = UUID.randomUUID();
        when(authorRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authorService.delete(id));
    }
}
