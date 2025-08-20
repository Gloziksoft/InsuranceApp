package com.insurance.app.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for event data related to insurance.
 */
public class EventDTO {

    private Long id; // event ID

    @NotNull(message = "Dátum udalosti je povinný.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime eventDate; // date when the event occurred

    @NotBlank(message = "Popis je povinný.")
    private String description; // description of the event

    @NotNull
    private Long insuranceId; // related insurance ID

    // Getters and setters

    /**
     * Gets the event ID.
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the event date.
     */
    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * Gets the event description.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the related insurance ID.
     */
    public Long getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(Long insuranceId) {
        this.insuranceId = insuranceId;
    }
}
