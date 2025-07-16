package com.poistenie.app.models.services;

import com.poistenie.app.data.entities.InsuranceEntity;
import com.poistenie.app.data.entities.InsuredPersonEntity;
import com.poistenie.app.data.repositories.InsuranceRepository;
import com.poistenie.app.data.repositories.InsuredPersonRepository;
import com.poistenie.app.models.dto.EventDTO;
import com.poistenie.app.models.dto.InsuranceDTO;
import com.poistenie.app.models.dto.mappers.InsuranceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Service implementation for managing insurance policies.
 */
@Service
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceRepository repository;
    private final InsuranceMapper mapper;
    private final InsuredPersonRepository insuredPersonRepository;
    private final EventService eventService;

    @Autowired
    public InsuranceServiceImpl(InsuranceRepository repository,
                                InsuranceMapper mapper,
                                InsuredPersonRepository insuredPersonRepository,
                                EventService eventService) {
        this.repository = repository;
        this.mapper = mapper;
        this.insuredPersonRepository = insuredPersonRepository;
        this.eventService = eventService;
    }

    /**
     * Finds an insurance policy by ID.
     */
    @Override
    public InsuranceDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }

    /**
     * Retrieves all insurance policies with pagination.
     */
    @Override
    public Page<InsuranceDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDTO);
    }

    /**
     * Searches for insurance policies by insured person's first or last name.
     */
    @Override
    public Page<InsuranceDTO> search(String keyword, Pageable pageable) {
        return repository.findByInsuredPerson_FirstNameContainingIgnoreCaseOrInsuredPerson_LastNameContainingIgnoreCase(
                keyword, keyword, pageable
        ).map(mapper::toDTO);
    }

    /**
     * Retrieves all insurance policies for a specific insured person.
     */
    @Override
    public List<InsuranceDTO> findByInsuredPersonId(Long insuredPersonId) {
        return repository.findByInsuredPersonId(insuredPersonId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new insurance policy and logs a creation event.
     */
    @Override
    public InsuranceDTO create(InsuranceDTO dto) {
        InsuranceEntity entity = mapper.toEntity(dto);

        InsuredPersonEntity insuredPerson = insuredPersonRepository.findById(dto.getInsuredPersonId())
                .orElseThrow(() -> new RuntimeException("Insured person does not exist."));
        entity.setInsuredPerson(insuredPerson);

        if (dto.getPolicyHolderId() != null) {
            InsuredPersonEntity policyHolder = insuredPersonRepository.findById(dto.getPolicyHolderId())
                    .orElseThrow(() -> new RuntimeException("Policy holder does not exist."));
            entity.setPolicyHolder(policyHolder);
        }

        InsuranceEntity savedInsurance = repository.save(entity);

        // Log creation event
        logInsuranceEvent(savedInsurance, "Vytvorenie poistenia pre");

        return mapper.toDTO(savedInsurance);
    }

    /**
     * Updates an existing insurance policy and logs an update event.
     */
    @Override
    public InsuranceDTO update(Long id, InsuranceDTO dto) {
        InsuranceEntity insurance = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Poistenie s ID " + id + " nebolo nájdené."));

        insurance.setInsuranceType(dto.getInsuranceType());
        insurance.setInsuredAmount(dto.getInsuredAmount());
        insurance.setStartDate(dto.getStartDate());
        insurance.setEndDate(dto.getEndDate());

        InsuredPersonEntity insuredPerson = insuredPersonRepository.findById(dto.getInsuredPersonId())
                .orElseThrow(() -> new RuntimeException("Poistenec neexistuje."));
        insurance.setInsuredPerson(insuredPerson);

        if (dto.getPolicyHolderId() != null) {
            InsuredPersonEntity policyHolder = insuredPersonRepository.findById(dto.getPolicyHolderId())
                    .orElseThrow(() -> new RuntimeException("Poistník neexistuje."));
            insurance.setPolicyHolder(policyHolder);
        } else {
            insurance.setPolicyHolder(null);
        }

        InsuranceEntity saved = repository.save(insurance);
        logInsuranceEvent(saved, "Úprava poistenia pre");

        return mapper.toDTO(saved);
    }

    /**
     * Deletes an insurance policy by ID and logs a deletion event.
     */
    @Override
    public void delete(Long id) {
        InsuranceEntity insurance = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Poistenie s ID " + id + " nebolo nájdené."));

        // Log deletion event
        logInsuranceEvent(insurance, "Vymazanie poistenia pre");

        repository.deleteById(id);
    }

    /**
     * Logs an event related to an insurance action.
     */
    private void logInsuranceEvent(InsuranceEntity insurance, String actionDescription) {
        if (insurance != null && insurance.getInsuredPerson() != null) {
            EventDTO event = new EventDTO();
            event.setInsuranceId(insurance.getId());
            event.setDescription(actionDescription + " " +
                    insurance.getInsuredPerson().getFirstName() + " " +
                    insurance.getInsuredPerson().getLastName());
            event.setEventDate(LocalDateTime.now());
            eventService.save(event);
        }
    }

    @Override
    public Page<InsuranceDTO> findByUserEmail(String email, Pageable pageable) {
        return repository.findByInsuredPerson_Email(email, pageable)
                .map(mapper::toDTO);
    }


    /**
     * Deletes invalid events based on description rules.
     * (currently unused, but method kept for future use)
     */
    @Override
    public void deleteEventsWithInvalidDescriptions() {
        eventService.deleteEventsWithInvalidDescriptions();
    }
}

