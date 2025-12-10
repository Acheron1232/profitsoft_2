package com.acheron.profitsoft2.mapper;

import com.acheron.profitsoft2.dto.response.AuthorDto;
import com.acheron.profitsoft2.dto.request.AuthorSaveDto;
import com.acheron.profitsoft2.entity.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AuthorMapper {

    public abstract AuthorDto map(Author author);

    public abstract Author map(AuthorDto authorDto);

    public abstract Author map(AuthorSaveDto authorSaveDto);
}
