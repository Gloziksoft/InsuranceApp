package com.insurance.app.models.services;

import com.insurance.app.models.dto.EventDTO;
import java.util.List;

/**
 * Service interface for managing events related to insurance.
 */
public interface EventService {

    /**
     * Finds all events associated with a specific insurance ID.
     */
    List<EventDTO> findByInsuranceId(Long insuranceId);

    /**
     * Saves a new event.
     */
    void save(EventDTO eventDTO);

    /**
     * Deletes all events with invalid descriptions containing "ID".
     */
    void deleteEventsWithInvalidDescriptions();

    List<EventDTO> findAll();
}
