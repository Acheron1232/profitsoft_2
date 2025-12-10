package com.acheron.profitsoft2.service;

import com.acheron.profitsoft2.dto.request.*;
import com.acheron.profitsoft2.dto.response.BookDto;
import com.acheron.profitsoft2.dto.response.UploadResultDto;
import com.acheron.profitsoft2.entity.Author;
import com.acheron.profitsoft2.entity.Book;
import com.acheron.profitsoft2.mapper.BookMapper;
import com.acheron.profitsoft2.repository.AuthorRepository;
import com.acheron.profitsoft2.repository.BookRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookService extends EntityService<Book, UUID> {
    private final BookMapper bookMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthorRepository authorRepository;

    public BookService(JpaRepository<Book, UUID> repository, BookMapper bookMapper,AuthorRepository authorRepository) {
        super(repository);
        this.bookMapper = bookMapper;
        this.authorRepository = authorRepository;
    }

    public ResponseEntity<BookDto> save(@Valid BookSaveDto bookSaveDto) {
        Book save = save(bookMapper.map(bookSaveDto));
        BookDto bookDto = bookMapper.map(save);
        return ResponseEntity.ok(bookDto);
    }

    public ResponseEntity<BookDto> findById(UUID id) {
        Book book = findEntityById(id);
        return ResponseEntity.ok(bookMapper.map(book));
    }

    public ResponseEntity<BookDto> update(UUID id, @Valid BookUpdateDto dto) {
        Book book = findEntityById(id);

        if (dto.title() != null && !dto.title().isBlank()) {
            book.setTitle(dto.title());
        }
        if (dto.isbn() != null && !dto.isbn().isBlank()) {
            book.setIsbn(dto.isbn());
        }

        Book saved = save(book);

        return ResponseEntity.ok(bookMapper.map(saved));
    }

    public void delete(UUID id) {
        deleteById(id);
    }

    public FilteredBooksResponse findAll(ListBookRequest request) {
        PageRequest pageable = PageRequest.of(request.page()!=null? request.page() : 0, request.size() != null ? request.size() : 10);
        Specification<Book> spec = request.toSpecification();

        BookRepository bookRepository = (BookRepository) repository;
        Page<Book> page = bookRepository.findAll(spec, pageable);

        List<BookDto> dtos = page.getContent()
                .stream()
                .map(bookMapper::map)
                .toList();

        return new FilteredBooksResponse(dtos, page.getTotalPages());
    }

    public byte[] exportAll(ListBookRequest request) {
        Specification<Book> spec = request.toSpecification();
        BookRepository bookRepository = (BookRepository) repository;
        List<Book> books = bookRepository.findAll(spec);
        List<BookDto> dtos = books.stream()
                .map(bookMapper::map)
                .toList();
        return exportCsv(dtos);
    }
    private byte[] exportCsv(List<BookDto> books) {
        StringBuilder sb = new StringBuilder();
        sb.append("title,isbn,publish_date,first_name,last_name\n");

        for (BookDto b : books) {
            sb.append(b.title()).append(",");
            sb.append(b.isbn()).append(",");
            sb.append(b.publishDate()).append(",");
            sb.append(b.author().firstName()).append(",");
            sb.append(b.author().lastName()).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public UploadResultDto uploadBooks(MultipartFile file) {

        List<String> errors = new ArrayList<>();
        int imported = 0;

        try {
            List<UploadBookDto> books = objectMapper.readValue(
                    file.getBytes(),
                    new TypeReference<>() {}
            );

            for (UploadBookDto dto : books) {
                try {
                    UUID authorId = UUID.fromString(dto.authorId());
                    Author author = authorRepository.findById(authorId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException("Author not found: " + dto.authorId())
                            );
                    Book book = new Book();
                    book.setAuthor(author);
                    book.setTitle(dto.title());
                    book.setIsbn(dto.isbn());
                    book.setPublishDate(dto.publishDate());

                    save(book);
                    imported++;

                } catch (Exception e) {
                    errors.add("Failed to import book '" + dto.title() + "': " + e.getMessage());
                }
            }

        } catch (Exception e) {
            return new UploadResultDto(0, 0, List.of("Invalid JSON file: " + e.getMessage()));
        }

        return new UploadResultDto(
                imported,
                errors.size(),
                errors
        );
    }




}