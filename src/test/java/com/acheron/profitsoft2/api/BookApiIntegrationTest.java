package com.acheron.profitsoft2.api;

import com.acheron.profitsoft2.dto.request.BookSaveDto;
import com.acheron.profitsoft2.dto.request.BookUpdateDto;
import com.acheron.profitsoft2.dto.request.FilteredBooksResponse;
import com.acheron.profitsoft2.dto.request.ListBookRequest;
import com.acheron.profitsoft2.dto.response.AuthorDto;
import com.acheron.profitsoft2.dto.response.BookDto;
import com.acheron.profitsoft2.dto.response.UploadResultDto;
import com.acheron.profitsoft2.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookApi.class)
class BookApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Provide sample BookSaveDto objects for parameterized tests
     */
    static Stream<BookSaveDto> bookSaveProvider() {
        return Stream.of(
                new BookSaveDto(UUID.randomUUID().toString(), "Book One", "1234567890"),
                new BookSaveDto(UUID.randomUUID().toString(), "Book Two", "1234567890123")
        );
    }

    @ParameterizedTest
    @MethodSource("bookSaveProvider")
    void saveBook_shouldReturnOk(BookSaveDto dto) throws Exception {
        BookDto responseDto = new BookDto(new AuthorDto("John", "Doe"), dto.title(), dto.isbn(), Instant.now());
        when(bookService.save(any(BookSaveDto.class))).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(bookService, times(1)).save(any(BookSaveDto.class));
    }

    @Test
    void getBookById_shouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        BookDto responseDto = new BookDto(new AuthorDto("John", "Doe"), "Title", "1234567890", Instant.now());

        when(bookService.findById(id)).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(get("/api/book/{id}", id))
                .andExpect(status().isOk());

        verify(bookService).findById(id);
    }

    @Test
    void updateBook_shouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        BookUpdateDto dto = new BookUpdateDto("Updated Title", "0987654321");
        BookDto responseDto = new BookDto(new AuthorDto("John", "Doe"), dto.title(), dto.isbn(), Instant.now());

        when(bookService.update(eq(id), any(BookUpdateDto.class))).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(put("/api/book/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(bookService).update(eq(id), any(BookUpdateDto.class));
    }

    @Test
    void deleteBook_shouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(bookService).delete(id);

        mockMvc.perform(delete("/api/book/{id}", id))
                .andExpect(status().isOk());

        verify(bookService).delete(id);
    }

    @Test
    void findAllBooks_shouldReturnOk() throws Exception {
        ListBookRequest request = new ListBookRequest(
                "Some title",
                null,
                "1234567890",
                UUID.randomUUID().toString(),
                0,
                10
        );
        FilteredBooksResponse response = new FilteredBooksResponse(List.of(), 1);

        when(bookService.findAll(any(ListBookRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/book/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookService).findAll(any(ListBookRequest.class));
    }

    @Test
    void exportBooks_shouldReturnOk() throws Exception {
        ListBookRequest request = new ListBookRequest(
                "Some title",
                null,
                "1234567890",
                UUID.randomUUID().toString(),
                0,
                10
        );
        byte[] fileBytes = "title,isbn\nBook1,1234567890".getBytes();

        when(bookService.exportAll(any(ListBookRequest.class))).thenReturn(fileBytes);

        mockMvc.perform(post("/api/book/_export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(bookService).exportAll(any(ListBookRequest.class));
    }

    @Test
    void uploadBooks_shouldReturnOk() throws Exception {
        String json = "[{\"authorId\":\"" + UUID.randomUUID() + "\",\"title\":\"Book1\",\"isbn\":\"1234567890\",\"publishDate\":\"2025-12-10T12:00:00Z\"}]";
        MockMultipartFile file = new MockMultipartFile("file", "books.json", "application/json", json.getBytes());

        UploadResultDto result = new UploadResultDto(1, 0, List.of());
        when(bookService.uploadBooks(any())).thenReturn(result);

        mockMvc.perform(multipart("/api/book/upload").file(file))
                .andExpect(status().isOk());

        verify(bookService).uploadBooks(any());
    }
}
