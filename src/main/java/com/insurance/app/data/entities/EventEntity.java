package com.insurance.app.data.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_entity")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime eventDate; // event date

    @Column(nullable = false, length = 500)
    private String description; // event description

    @ManyToOne(optional = false)
    @JoinColumn(name = "insurance_id", nullable = false)
    private InsuranceEntity insurance; // related insurance

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InsuranceEntity getInsurance() {
        return insurance;
    }

    public void setInsurance(InsuranceEntity insurance) {
        this.insurance = insurance;
    }
}
