package com.acheron.profitsoft2.service;

import com.acheron.profitsoft2.dto.request.BookSaveDto;
import com.acheron.profitsoft2.dto.request.BookUpdateDto;
import com.acheron.profitsoft2.dto.response.BookDto;
import com.acheron.profitsoft2.dto.response.UploadResultDto;
import com.acheron.profitsoft2.entity.Author;
import com.acheron.profitsoft2.entity.Book;
import com.acheron.profitsoft2.exception.EntityNotFoundException;
import com.acheron.profitsoft2.mapper.BookMapper;
import com.acheron.profitsoft2.repository.AuthorRepository;
import com.acheron.profitsoft2.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private BookMapper bookMapper;
    private BookService bookService;

    @BeforeEach
    void setup() {
        bookRepository = mock(BookRepository.class);
        authorRepository = mock(AuthorRepository.class);
        bookMapper = mock(BookMapper.class);
        bookService = new BookService(bookRepository, bookMapper, authorRepository);
    }

    @Test
    void saveBook_shouldReturnDto() {
        BookSaveDto dto = new BookSaveDto(UUID.randomUUID().toString(),"Title", "1234567890" );
        Book book = new Book();
        Book savedBook = new Book();
        BookDto bookDto = mock(BookDto.class);

        when(bookMapper.map(dto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.map(savedBook)).thenReturn(bookDto);

        BookDto result = bookService.save(dto).getBody();
        assertNotNull(result);
        verify(bookRepository).save(book);
    }

    @Test
    void findById_existingBook_returnsDto() {
        UUID id = UUID.randomUUID();
        Book book = new Book();
        BookDto bookDto = mock(BookDto.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookMapper.map(book)).thenReturn(bookDto);

        BookDto result = bookService.findById(id).getBody();
        assertNotNull(result);
        verify(bookRepository).findById(id);
    }

    @Test
    void findById_nonExistingBook_throwsException() {
        UUID id = UUID.randomUUID();
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookService.findById(id));
    }

    @Test
    void updateBook_shouldUpdateFields() {
        UUID id = UUID.randomUUID();
        BookUpdateDto dto = new BookUpdateDto("Updated Title", "0987654321");
        Book book = new Book();
        Book savedBook = new Book();
        BookDto bookDto = mock(BookDto.class);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.map(savedBook)).thenReturn(bookDto);

        BookDto result = bookService.update(id, dto).getBody();
        assertNotNull(result);
        assertEquals("Updated Title", book.getTitle());
        assertEquals("0987654321", book.getIsbn());
    }

    @Test
    void uploadBooks_shouldImportBooks() throws Exception {
        UUID authorId = UUID.randomUUID();
        Author author = new Author();
        when(authorRepository.findById(authorId)).thenReturn(Optional.of(author));

        String json = "[{\"title\":\"Book1\",\"isbn\":\"1234567890\",\"authorId\":\"" + authorId + "\",\"publishDate\":\"2025-12-10T10:00:00Z\"}]";
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn(json.getBytes());

        UploadResultDto result = bookService.uploadBooks(file);
        assertEquals(1, result.imported());
        assertEquals(0, result.errors().size());
    }
}
