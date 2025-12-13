package com.acheron.profitsoft2.service;

import com.acheron.profitsoft2.dto.request.*;
import com.acheron.profitsoft2.dto.response.BookDto;
import com.acheron.profitsoft2.dto.response.UploadResultDto;
import com.acheron.profitsoft2.entity.Author;
import com.acheron.profitsoft2.entity.Book;
import com.acheron.profitsoft2.exception.EntityNotFoundException;
import com.acheron.profitsoft2.mapper.BookMapper;
import com.acheron.profitsoft2.repository.AuthorRepository;
import com.acheron.profitsoft2.repository.BookRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling Book entity operations with logging.
 */
@Service
public class BookService extends EntityService<Book, UUID> {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookMapper bookMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, BookMapper bookMapper, AuthorRepository authorRepository) {
        super(bookRepository);
        this.bookMapper = bookMapper;
        this.authorRepository = authorRepository;
    }

    /**
     * Save a new book.
     */
    public ResponseEntity<BookDto> save(@Valid BookSaveDto dto) {
        log.info("Saving new book: {}", dto.title());
        Book book = bookMapper.map(dto);
        Book saved = save(book);
        log.info("Saved book with ID: {}", saved.getId());
        return ResponseEntity.ok(bookMapper.map(saved));
    }

    /**
     * Find book by ID.
     */
    public ResponseEntity<BookDto> findById(UUID id) {
        log.info("Finding book with ID: {}", id);
        Book book = findEntityById(id);
        return ResponseEntity.ok(bookMapper.map(book));
    }

    /**
     * Update book by ID.
     */
    public ResponseEntity<BookDto> update(UUID id, @Valid BookUpdateDto dto) {
        log.info("Updating book with ID: {}", id);
        Book book = findEntityById(id);

        if (dto.title() != null && !dto.title().isBlank()) book.setTitle(dto.title());
        if (dto.isbn() != null && !dto.isbn().isBlank()) book.setIsbn(dto.isbn());

        Book saved = save(book);
        log.info("Updated book with ID: {}", id);
        return ResponseEntity.ok(bookMapper.map(saved));
    }

    /**
     * Delete book by ID.
     */
    public void delete(UUID id) {
        log.info("Deleting book with ID: {}", id);
        deleteById(id);
        log.info("Deleted book with ID: {}", id);
    }

    /**
     * Find books with filtering and pagination.
     */
    public FilteredBooksResponse findAll(ListBookRequest request) {
        log.info("Fetching books with filters: page={}, size={}", request.page(), request.size());
        PageRequest pageable = PageRequest.of(request.page() != null ? request.page() : 0,
                request.size() != null ? request.size() : 10);

        Specification<Book> spec = request.toSpecification();
        BookRepository bookRepository = (BookRepository) repository;

        Page<Book> page = bookRepository.findAll(spec, pageable);
        List<BookDto> dtos = page.getContent().stream().map(bookMapper::map).toList();

        log.info("Fetched {} books (total pages: {})", dtos.size(), page.getTotalPages());
        return new FilteredBooksResponse(dtos, page.getTotalPages());
    }

    /**
     * Export filtered books as CSV.
     */
    public byte[] exportAll(ListBookRequest request) {
        log.info("Exporting books to CSV");
        Specification<Book> spec = request.toSpecification();
        BookRepository bookRepository = (BookRepository) repository;

        List<BookDto> dtos = bookRepository.findAll(spec)
                .stream()
                .map(bookMapper::map)
                .toList();

        log.info("Exported {} books", dtos.size());
        return exportCsv(dtos);
    }

    private byte[] exportCsv(List<BookDto> books) {
        StringBuilder sb = new StringBuilder();
        sb.append("title,isbn,publish_date,author_first_name,author_last_name\n");

        for (BookDto b : books) {
            sb.append(b.title()).append(",")
                    .append(b.isbn()).append(",")
                    .append(b.publishDate()).append(",")
                    .append(b.author().firstName()).append(",")
                    .append(b.author().lastName()).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Upload books from JSON file.
     */
    public UploadResultDto uploadBooks(MultipartFile file) {
        log.info("Uploading books from file: {}", file.getOriginalFilename());
        List<String> errors = new ArrayList<>();
        int imported = 0;

        try {
            List<UploadBookDto> books = objectMapper.readValue(file.getBytes(), new TypeReference<>() {
            });
            for (UploadBookDto dto : books) {
                try {
                    UUID authorId = UUID.fromString(dto.authorId());
                    Author author = authorRepository.findById(authorId)
                            .orElseThrow(() -> {
                                log.warn("Author not found: {}", authorId);
                                return new EntityNotFoundException("Author not found: " + authorId);
                            });

                    Book book = new Book();
                    book.setAuthor(author);
                    book.setTitle(dto.title());
                    book.setIsbn(dto.isbn());
                    book.setPublishDate(dto.publishDate());

                    save(book);
                    imported++;
                    log.info("Imported book: {}", dto.title());
                } catch (Exception e) {
                    errors.add("Failed to import book '" + dto.title() + "': " + e.getMessage());
                    log.warn("Failed to import book '{}': {}", dto.title(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Invalid JSON file: {}", e.getMessage());
            return new UploadResultDto(0, 0, List.of("Invalid JSON file: " + e.getMessage()));
        }

        log.info("Imported {} books, {} errors", imported, errors.size());
        return new UploadResultDto(imported, errors.size(), errors);
    }
}