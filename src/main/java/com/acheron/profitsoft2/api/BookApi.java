package com.acheron.profitsoft2.api;

import com.acheron.profitsoft2.dto.request.*;
import com.acheron.profitsoft2.dto.response.BookDto;
import com.acheron.profitsoft2.dto.response.UploadResultDto;
import com.acheron.profitsoft2.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookApi {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDto> save(@RequestBody @Valid BookSaveDto bookSaveDto) {
        return bookService.save(bookSaveDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> findById(@PathVariable UUID id) {
        return bookService.findById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> update(@PathVariable UUID id,
                                          @Valid @RequestBody BookUpdateDto bookUpdateDto) {
        return bookService.update(id, bookUpdateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        bookService.delete(id);
        return ResponseEntity.ok(id.toString());
    }

    @PostMapping("/_list")
    public ResponseEntity<FilteredBooksResponse> findAll(
            @RequestBody ListBookRequest request) {

        return ResponseEntity.ok(bookService.findAll(request));
    }

    @PostMapping("/_export")
    public ResponseEntity<byte[]> export(
            @RequestBody ListBookRequest request) {
        byte[] file = bookService.exportAll(request);
        String filename = "books_" + System.currentTimeMillis() + ".csv";


        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .header("Content-Type", "text/csv")
                .body(file);
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResultDto> uploadBooks(
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(bookService.uploadBooks(file));
    }
}
