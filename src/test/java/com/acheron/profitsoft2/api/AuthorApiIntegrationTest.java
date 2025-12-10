package com.acheron.profitsoft2.api;

import com.acheron.profitsoft2.dto.request.AuthorSaveDto;
import com.acheron.profitsoft2.dto.request.AuthorUpdateDto;
import com.acheron.profitsoft2.dto.response.AuthorDto;
import com.acheron.profitsoft2.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorApi.class)
class AuthorApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAuthors_shouldReturnOk() throws Exception {
        Mockito.when(authorService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/author"))
                .andExpect(status().isOk());
    }

    @Test
    void createAuthor_shouldReturnOk() throws Exception {
        AuthorSaveDto dto = new AuthorSaveDto("contact", "John", "Doe");
        AuthorDto responseDto = new AuthorDto( "John", "Doe");

        Mockito.when(authorService.save(Mockito.any())).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateAuthor_shouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        AuthorUpdateDto dto = new AuthorUpdateDto("contact", "Jane", "Smith");
        AuthorDto responseDto = new AuthorDto( "Jane", "Smith");

        Mockito.when(authorService.update(Mockito.eq(id), Mockito.any())).thenReturn(ResponseEntity.ok(responseDto));

        mockMvc.perform(put("/api/author/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAuthor_shouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(authorService).delete(id);

        mockMvc.perform(delete("/api/author/{id}", id))
                .andExpect(status().isOk());
    }
}
