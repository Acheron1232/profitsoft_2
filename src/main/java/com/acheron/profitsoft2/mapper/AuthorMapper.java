package com.acheron.profitsoft2.mapper;

import com.acheron.profitsoft2.dto.request.AuthorSaveDto;
import com.acheron.profitsoft2.dto.response.AuthorDto;
import com.acheron.profitsoft2.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Mapper for converting between Author entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDto toDto(Author author);

    Author toEntity(AuthorSaveDto authorSaveDto);

    void updateEntity(@MappingTarget Author author, AuthorDto authorDto);
}
