package com.acheron.profitsoft2.mapper;

import com.acheron.profitsoft2.dto.request.BookSaveDto;
import com.acheron.profitsoft2.dto.response.BookDto;
import com.acheron.profitsoft2.entity.Author;
import com.acheron.profitsoft2.entity.Book;
import com.acheron.profitsoft2.repository.AuthorRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;


@Mapper(componentModel = "spring",uses = AuthorMapper.class)
public abstract class BookMapper {

    @Autowired
    protected AuthorRepository authorRepository;

    public abstract BookDto map(Book book);

    public abstract Book map(BookDto bookDto);

    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "publishDate", expression = "java(java.time.Instant.now())")
    public abstract Book map(BookSaveDto bookSaveDto);

    protected Author map(String authorId) {
        return authorRepository.findById(UUID.fromString(authorId))
                .orElseThrow(() -> new IllegalArgumentException("Author not found: " + authorId));
    }

}
