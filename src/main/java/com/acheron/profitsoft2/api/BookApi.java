package com.acheron.profitsoft2.api;

import com.acheron.profitsoft2.dto.request.*;
import com.acheron.profitsoft2.dto.response.BookDto;
import com.acheron.profitsoft2.dto.response.UploadResultDto;
import com.acheron.profitsoft2.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * REST API for managing Book entities.
 */
@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookApi {

    private final BookService bookService;

    /** Create a new book */
    @PostMapping
    public ResponseEntity<BookDto> save(@RequestBody @Valid BookSaveDto dto) {
        return bookService.save(dto);
    }

    /** Get a book by ID */
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> findById(@PathVariable UUID id) {
        return bookService.findById(id);
    }

    /** Update a book by ID */
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> update(@PathVariable UUID id,
                                          @RequestBody @Valid BookUpdateDto dto) {
        return bookService.update(id, dto);
    }

    /** Delete a book by ID */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        bookService.delete(id);
        return ResponseEntity.ok(id.toString());
    }

    /** List books with filters and pagination */
    @PostMapping("/_list")
    public ResponseEntity<FilteredBooksResponse> findAll(@RequestBody ListBookRequest request) {
        return ResponseEntity.ok(bookService.findAll(request));
    }

    /** Export filtered books as CSV */
    @PostMapping("/_export")
    public ResponseEntity<byte[]> export(@RequestBody ListBookRequest request) {
        byte[] file = bookService.exportAll(request);
        String filename = "books_" + System.currentTimeMillis() + ".csv";

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.TEXT_PLAIN)
                .body(file);
    }

    /** Upload books from a JSON file */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResultDto> uploadBooks(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(bookService.uploadBooks(file));
    }
}
