package com.acheron.profitsoft2.dto.request;

import org.springframework.data.jpa.domain.Specification;

public interface Filterable<T> {
    Specification<T> toSpecification();
}
