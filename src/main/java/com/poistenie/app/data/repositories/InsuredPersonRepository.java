package com.poistenie.app.data.repositories;

import com.poistenie.app.data.entities.InsuredPersonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing InsuredPersonEntity persistence.
 */
@Repository
public interface InsuredPersonRepository extends JpaRepository<InsuredPersonEntity, Long> {

    /**
     * Searches insured persons by first name or last name (case-insensitive).
     */
    Page<InsuredPersonEntity> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable
    );

    /**
     *
     * @param email
     * @param pageable
     * @return
     */
    Page<InsuredPersonEntity> findByEmail(String email, Pageable pageable);
}
