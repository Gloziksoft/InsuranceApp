package com.insurance.app.data.repositories;

import com.insurance.app.data.entities.InsuredPersonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
     * Finds insured persons by email.
     */
    Page<InsuredPersonEntity> findByEmail(String email, Pageable pageable);

    /**
     * ✅ Fetch join – načíta poistenca aj s poistkami (a prípadne aj policyHolder).
     */
    @Query("SELECT ip FROM InsuredPersonEntity ip " +
            "LEFT JOIN FETCH ip.insurances ins " +
            "LEFT JOIN FETCH ins.policyHolder " +
            "WHERE ip.id = :id")
    Optional<InsuredPersonEntity> findByIdWithInsurances(@Param("id") Long id);
}