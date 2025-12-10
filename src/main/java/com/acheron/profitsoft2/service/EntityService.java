package com.acheron.profitsoft2.service;

import com.acheron.profitsoft2.exception.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class EntityService<E, ID> {

    protected final JpaRepository<E, ID> repository;

    public EntityService(JpaRepository<E, ID> repository) {
        this.repository = repository;
    }

    protected E findEntityById(ID id) {
        String service = this.getClass().getSimpleName().replace("Service", "");
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(service + " not found: " + id));
    }

    protected List<E> findAll() {
        return repository.findAll();
    }

    protected E save(@Valid E entity) {
        return repository.save(entity);
    }

    protected void deleteById(ID id) {
        repository.deleteById(id);
    }

    protected void deleteAll() {
        repository.deleteAll();
    }
}
