package com.insurance.app.models.services;

import com.insurance.app.data.entities.InsuredPersonEntity;
import com.insurance.app.data.repositories.InsuredPersonRepository;
import com.insurance.app.models.dto.InsuredPersonDTO;
import com.insurance.app.models.dto.mappers.InsuredPersonMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Service implementation for managing insured persons.
 */
@Service
public class InsuredPersonServiceImpl implements InsuredPersonService {

    private final InsuredPersonRepository repository;
    private final InsuredPersonMapper mapper;

    public InsuredPersonServiceImpl(InsuredPersonRepository repository,
                                    InsuredPersonMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Searches insured persons by name (first name or last name).
     */
    @Override
    public Page<InsuredPersonDTO> searchByName(String name, Pageable pageable) {
        return repository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable)
                .map(mapper::toDTO);
    }

    @Override
    public Page<InsuredPersonDTO> findByEmail(String email, Pageable pageable) {
        return repository.findByEmail(email, pageable)
                .map(mapper::toDTO);
    }

    /**
     * Retrieves all insured persons with pagination support.
     */
    @Override
    public Page<InsuredPersonDTO> getAllPaginated(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDTO);
    }

    /**
     * Finds an insured person by ID.
     * Throws NoSuchElementException if not found.
     */
    @Transactional(readOnly = true)
    public InsuredPersonDTO findById(Long id) {
        InsuredPersonEntity entity = repository.findByIdWithInsurances(id)
                .orElseThrow(() -> new NoSuchElementException("Insured person not found"));
        return mapper.toDTOWithInsurances(entity);
    }

    /**
     * Retrieves all insured persons without pagination.
     */
    @Override
    public List<InsuredPersonDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new insured person.
     */
    @Override
    public InsuredPersonDTO create(InsuredPersonDTO dto) {
        InsuredPersonEntity entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    /**
     * Updates an existing insured person by ID.
     */
    @Override
    public InsuredPersonDTO update(Long id, InsuredPersonDTO dto) {
        repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("The insured person with ID " + id + " does not exist."));

        InsuredPersonEntity updatedEntity = mapper.toEntity(dto);
        updatedEntity.setId(id);

        return mapper.toDTO(repository.save(updatedEntity));
    }

    /**
     * Deletes an insured person by ID.
     */
    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("The insured person with ID " + id + " does not exist.");
        }
        repository.deleteById(id);
    }
}
