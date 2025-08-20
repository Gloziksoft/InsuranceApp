package com.insurance.app.models.services;

import com.insurance.app.data.entities.EventEntity;
import com.insurance.app.data.repositories.EventRepository;
import com.insurance.app.models.dto.EventDTO;
import com.insurance.app.models.dto.mappers.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing events related to insurance.
 */
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    /**
     * Finds all events associated with a specific insurance ID.
     */
    @Override
    public List<EventDTO> findByInsuranceId(Long insuranceId) {
        if (insuranceId == null) {
            return Collections.emptyList();
        }

        List<EventEntity> events = eventRepository.findByInsuranceId(insuranceId);
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Saves a new event into the database.
     */
    @Override
    public void save(EventDTO eventDTO) {
        EventEntity eventEntity = eventMapper.toEntity(eventDTO);
        eventRepository.save(eventEntity);
    }

    /**
     * Deletes events that have invalid descriptions containing "ID".
     */
    @Override
    @Transactional
    public void deleteEventsWithInvalidDescriptions() {
        List<EventEntity> events = eventRepository.findAll();
        List<EventEntity> invalidEvents = events.stream()
                .filter(e -> e.getDescription() != null && e.getDescription().contains("ID"))
                .collect(Collectors.toList());

        for (EventEntity event : invalidEvents) {
            eventRepository.delete(event);
        }
    }

    @Override
    public List<EventDTO> findAll() {
        return eventRepository.findAll()
                .stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }
}
