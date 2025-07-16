package com.poistenie.app.models.services;

import com.poistenie.app.data.entities.InsuredPersonEntity;
import com.poistenie.app.models.dto.InsuredPersonDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing insured persons.
 */
public interface InsuredPersonService {

    /**
     * Searches insured persons by first or last name.
     */
    Page<InsuredPersonDTO> searchByName(String name, Pageable pageable);

    /**
     * Finds an insured person by email with pagination.
     */
    Page<InsuredPersonDTO> findByEmail(String email, Pageable pageable);

    /**
     * Retrieves all insured persons with pagination.
     */
    Page<InsuredPersonDTO> getAllPaginated(Pageable pageable);

    /**
     * Finds an insured person by ID.
     */
    InsuredPersonDTO findById(Long id);

    /**
     * Retrieves all insured persons without pagination.
     */
    List<InsuredPersonDTO> findAll();

    /**
     * Creates a new insured person.
     */
    InsuredPersonDTO create(InsuredPersonDTO dto);

    /**
     * Updates an existing insured person by ID.
     */
    InsuredPersonDTO update(Long id, InsuredPersonDTO dto);

    /**
     * Deletes an insured person by ID.
     */
    void delete(Long id);
}
