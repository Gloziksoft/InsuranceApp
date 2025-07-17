package com.poistenie.app.data.entities;

import com.poistenie.app.data.enums.InsuranceType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents an insurance policy for an insured person.
 */
@Entity
public class InsuranceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of the insurance (e.g., Life, Property, Health).
     */
    @Enumerated(EnumType.STRING)
    private InsuranceType insuranceType;

    /**
     * Start date when the insurance coverage becomes active.
     */
    private LocalDate startDate;

    /**
     * End date when the insurance coverage expires.
     */
    private LocalDate endDate;

    /**
     * Total amount covered by the insurance.
     */
    private BigDecimal insuredAmount;

    /**
     * The person who is covered by the insurance (insured).
     */
    @ManyToOne
    @JoinColumn(name = "insured_person_id")
    private InsuredPersonEntity insuredPerson;

    /**
     * The person who owns or manages the insurance contract (policy holder).
     */
    @ManyToOne
    @JoinColumn(name = "policy_holder_id")
    private InsuredPersonEntity policyHolder;

    /**
     * List of events associated with this insurance.
     */
    @OneToMany(mappedBy = "insurance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventEntity> events;

    // --- Getters and Setters ---

    /**
     * Gets the insurance ID.
     */
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    /**
     * Gets the type of insurance.
     */
    public InsuranceType getInsuranceType() { return insuranceType; }

    public void setInsuranceType(InsuranceType insuranceType) { this.insuranceType = insuranceType; }

    /**
     * Gets the start date of the insurance policy.
     */
    public LocalDate getStartDate() { return startDate; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    /**
     * Gets the end date of the insurance policy.
     */
    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    /**
     * Gets the total insured amount.
     */
    public BigDecimal getInsuredAmount() { return insuredAmount; }

    public void setInsuredAmount(BigDecimal insuredAmount) { this.insuredAmount = insuredAmount; }

    /**
     * Gets the insured person associated with this insurance.
     */
    public InsuredPersonEntity getInsuredPerson() { return insuredPerson; }

    public void setInsuredPerson(InsuredPersonEntity insuredPerson) { this.insuredPerson = insuredPerson; }

    /**
     * Gets the policy holder who manages the insurance.
     */
    public InsuredPersonEntity getPolicyHolder() { return policyHolder; }

    public void setPolicyHolder(InsuredPersonEntity policyHolder) { this.policyHolder = policyHolder; }

    /**
     * Returns the list of associated event entities.
     */
    public List<EventEntity> getEvents() {
        return events;
    }

    /**
     * Sets the list of associated event entities.
     */
    public void setEvents(List<EventEntity> events) {
        this.events = events;
    }
}
