package com.poistenie.app.data.repositories;

import com.poistenie.app.data.entities.InsuranceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InsuranceRepository extends JpaRepository<InsuranceEntity, Long> {

    // Nájde všetky poistenia pre danú ID poistenca
    List<InsuranceEntity> findByInsuredPersonId(Long insuredPersonId);

    // Vyhľadávanie podľa mena alebo priezviska poistenca
    Page<InsuranceEntity> findByInsuredPerson_FirstNameContainingIgnoreCaseOrInsuredPerson_LastNameContainingIgnoreCase(
            String firstName,
            String lastName,
            Pageable pageable
    );

    // ⚠️ Nová metóda: Nájde všetky poistenia, kde poistenec má tento email
    Page<InsuranceEntity> findByInsuredPerson_Email(String email, Pageable pageable);
}
