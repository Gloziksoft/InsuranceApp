package com.insurance.app.data.repositories;

import com.insurance.app.data.entities.InsuranceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsuranceRepository extends JpaRepository<InsuranceEntity, Long> {

    List<InsuranceEntity> findByInsuredPersonId(Long insuredPersonId);

    Page<InsuranceEntity> findByInsuredPerson_FirstNameContainingIgnoreCaseOrInsuredPerson_LastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable
    );

    Page<InsuranceEntity> findByInsuredPerson_Email(String email, Pageable pageable);

    // ✅ Nová metóda – detail s fetch join
    @Query("SELECT i FROM InsuranceEntity i " +
            "LEFT JOIN FETCH i.insuredPerson " +
            "LEFT JOIN FETCH i.policyHolder " +
            "WHERE i.id = :id")
    Optional<InsuranceEntity> findByIdWithRelations(@Param("id") Long id);
}
