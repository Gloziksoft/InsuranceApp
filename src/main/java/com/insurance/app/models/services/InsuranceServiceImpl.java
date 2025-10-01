package com.insurance.app.models.services;

import com.insurance.app.data.entities.InsuranceEntity;
import com.insurance.app.data.entities.InsuredPersonEntity;
import com.insurance.app.data.repositories.InsuranceRepository;
import com.insurance.app.data.repositories.InsuredPersonRepository;
import com.insurance.app.models.dto.EventDTO;
import com.insurance.app.models.dto.InsuranceDTO;
import com.insurance.app.models.dto.mappers.InsuranceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Finds an insurance policy by its ID.
     * @param id the ID of the insurance policy
     * @return the InsuranceDTO if found, otherwise null
     */
    @Override
    @Transactional(readOnly = true)
    public InsuranceDTO findById(Long id) {
        InsuranceEntity entity = repository.findByIdWithRelations(id)
                .orElseThrow(() -> new NoSuchElementException("Insurance with ID " + id + " was not found."));

        return mapper.toDTO(entity);
    }

    /**
     * Retrieves all insurance policies with pagination support.
     * @param pageable pagination information
     * @return a page of InsuranceDTO objects
     */
    @Override
    public Page<InsuranceDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDTO);
    }

    /**
     * Searches insurance policies by insured person's first or last name (case-insensitive).
     * @param keyword the search keyword
     * @param pageable pagination information
     * @return a page of matching InsuranceDTO objects
     */
    @Override
    public Page<InsuranceDTO> search(String keyword, Pageable pageable) {
        return repository.findByInsuredPerson_FirstNameContainingIgnoreCaseOrInsuredPerson_LastNameContainingIgnoreCase(
                keyword, keyword, pageable
        ).map(mapper::toDTO);
    }

    /**
     * Retrieves all insurance policies for a specific insured person by their ID.
     * @param insuredPersonId the insured person's ID
     * @return list of InsuranceDTO objects
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
     * @param dto the insurance data transfer object
     * @return the created InsuranceDTO
     */
    @Override
    public InsuranceDTO create(InsuranceDTO dto) {
        InsuranceEntity entity = mapper.toEntity(dto);

        // Fetch the insured person entity; throw if not found
        InsuredPersonEntity insuredPerson = insuredPersonRepository.findById(dto.getInsuredPersonId())
                .orElseThrow(() -> new RuntimeException("Insured person does not exist."));
        entity.setInsuredPerson(insuredPerson);

        // Optional: set the policy holder if provided
        if (dto.getPolicyHolderId() != null) {
            InsuredPersonEntity policyHolder = insuredPersonRepository.findById(dto.getPolicyHolderId())
                    .orElseThrow(() -> new RuntimeException("Policy holder does not exist."));
            entity.setPolicyHolder(policyHolder);
        }

        // Save insurance entity to repository
        InsuranceEntity savedInsurance = repository.save(entity);

        // Log creation event
        logInsuranceEvent(savedInsurance, "Creation of insurance for");

        return mapper.toDTO(savedInsurance);
    }

    /**
     * Updates an existing insurance policy and logs an update event.
     * @param id the ID of the insurance policy
     * @param dto the updated insurance DTO
     * @return the updated InsuranceDTO
     */
    @Override
    public InsuranceDTO update(Long id, InsuranceDTO dto) {
        InsuranceEntity insurance = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Insurance with ID " + id + " was not found."));

        // Update insurance fields
        insurance.setInsuranceType(dto.getInsuranceType());
        insurance.setInsuredAmount(dto.getInsuredAmount());
        insurance.setStartDate(dto.getStartDate());
        insurance.setEndDate(dto.getEndDate());

        // Update insured person; throw if not found
        InsuredPersonEntity insuredPerson = insuredPersonRepository.findById(dto.getInsuredPersonId())
                .orElseThrow(() -> new RuntimeException("Insured person does not exist."));
        insurance.setInsuredPerson(insuredPerson);

        // Update policy holder if provided
        if (dto.getPolicyHolderId() != null) {
            InsuredPersonEntity policyHolder = insuredPersonRepository.findById(dto.getPolicyHolderId())
                    .orElseThrow(() -> new RuntimeException("Policy holder does not exist."));
            insurance.setPolicyHolder(policyHolder);
        } else {
            insurance.setPolicyHolder(null);
        }

        // Save updated insurance and log the event
        InsuranceEntity saved = repository.save(insurance);
        logInsuranceEvent(saved, "Update of insurance for");

        return mapper.toDTO(saved);
    }

    /**
     * Deletes an insurance policy by ID and logs a deletion event.
     * @param id the ID of the insurance policy to delete
     */
    @Override
    public void delete(Long id) {
        InsuranceEntity insurance = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Insurance with ID " + id + " was not found."));

        // Log deletion event
        logInsuranceEvent(insurance, "Deletion of insurance for");

        repository.deleteById(id);
    }

    /**
     * Logs an insurance-related event with description.
     * @param insurance the insurance entity
     * @param actionDescription description of the action (e.g., creation, update, deletion)
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

    /**
     * Finds insurance policies associated with a user's email.
     * @param email user's email
     * @param pageable pagination info
     * @return a page of InsuranceDTO objects
     */
    @Override
    public Page<InsuranceDTO> findByUserEmail(String email, Pageable pageable) {
        return repository.findByInsuredPerson_Email(email, pageable)
                .map(mapper::toDTO);
    }

    /**
     * Deletes invalid events based on description rules.
     * (currently unused, but kept for potential future use)
     */
    @Override
    public void deleteEventsWithInvalidDescriptions() {
        eventService.deleteEventsWithInvalidDescriptions();
    }
}
