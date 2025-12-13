package com.acheron.profitsoft2.api;

import com.acheron.profitsoft2.dto.request.BookSaveDto;
import com.acheron.profitsoft2.dto.request.BookUpdateDto;
import com.acheron.profitsoft2.dto.request.FilteredBooksResponse;
import com.acheron.profitsoft2.dto.request.ListBookRequest;
import com.acheron.profitsoft2.dto.response.BookDto;
import com.acheron.profitsoft2.dto.response.UploadResultDto;
import com.acheron.profitsoft2.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * REST API for managing Book entities with logging.
 */
@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookApi {

    private static final Logger log = LoggerFactory.getLogger(BookApi.class);

    private final BookService bookService;

    /**
     * Create a new book
     */
    @PostMapping
    public ResponseEntity<BookDto> save(@RequestBody @Valid BookSaveDto dto) {
        log.info("API call: Save book '{}'", dto.title());
        return bookService.save(dto);
    }

    /**
     * Get a book by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> findById(@PathVariable UUID id) {
        log.info("API call: Find book by ID {}", id);
        ResponseEntity<BookDto> response = bookService.findById(id);
        log.info("Book found: {}", response.getBody().title());
        return response;
    }

    /**
     * Update a book by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> update(@PathVariable UUID id,
                                          @RequestBody @Valid BookUpdateDto dto) {
        log.info("API call: Update book ID {}", id);
        ResponseEntity<BookDto> response = bookService.update(id, dto);
        log.info("Book updated: {}", response.getBody().title());
        return response;
    }

    /**
     * Delete a book by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        log.info("API call: Delete book ID {}", id);
        bookService.delete(id);
        log.info("Book deleted ID {}", id);
        return ResponseEntity.ok(id.toString());
    }

    /**
     * List books with filters and pagination
     */
    @PostMapping("/_list")
    public ResponseEntity<FilteredBooksResponse> findAll(@RequestBody ListBookRequest request) {
        log.info("API call: List books with filters, page={}, size={}", request.page(), request.size());
        ResponseEntity<FilteredBooksResponse> response = ResponseEntity.ok(bookService.findAll(request));
        log.info("Books listed: {} items", response.getBody().books().size());
        return response;
    }

    /**
     * Export filtered books as CSV
     */
    @PostMapping("/_export")
    public ResponseEntity<byte[]> export(@RequestBody ListBookRequest request) {
        log.info("API call: Export books as CSV, page={}, size={}", request.page(), request.size());
        byte[] file = bookService.exportAll(request);
        String filename = "books_" + System.currentTimeMillis() + ".csv";
        log.info("Exported {} bytes as file '{}'", file.length, filename);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.TEXT_PLAIN)
                .body(file);
    }

    /**
     * Upload books from a JSON file
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResultDto> uploadBooks(@RequestParam("file") MultipartFile file) {
        log.info("API call: Upload books from file '{}'", file.getOriginalFilename());
        ResponseEntity<UploadResultDto> response = ResponseEntity.ok(bookService.uploadBooks(file));
        log.info("Books uploaded: imported={}, errors={}", response.getBody().imported(), response.getBody().errors().size());
        return response;
    }
}