package com.poistenie.app.models.dto;

import com.poistenie.app.data.enums.InsuranceType;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for insurance data.
 */
public class InsuranceDTO {

    @NotNull(message = "Poistený je povinný.")
    private Long insuredPersonId; // insured person ID

    @NotNull(message = "Poistník je povinný.")
    private Long policyHolderId; // policy holder ID

    private Long id; // insurance ID

    @NotNull(message = "Typ poistenia je povinný.")
    private InsuranceType insuranceType; // insurance type

    @NotNull(message = "Dátum začiatku je povinný")
    @FutureOrPresent(message = "Dátum začiatku musí byť dnes alebo v budúcnosti")
    private LocalDate startDate;

    @NotNull(message = "Dátum konca je povinný")
    @Future(message = "Dátum konca musí byť v budúcnosti")
    private LocalDate endDate;

    @NotNull(message = "Čiastka je povinná.")
    @Positive(message = "Čiastka musí byť kladné číslo.")
    private BigDecimal insuredAmount; // insured amount

    private String insuredPersonFirstName; // insured person's first name
    private String insuredPersonLastName; // insured person's last name

    private String policyHolderFirstName; // policy holder's first name
    private String policyHolderLastName; // policy holder's last name

    // Getters and setters
    public Long getInsuredPersonId() {
        return insuredPersonId;
    }

    public void setInsuredPersonId(Long insuredPersonId) {
        this.insuredPersonId = insuredPersonId;
    }

    public Long getPolicyHolderId() {
        return policyHolderId;
    }

    public void setPolicyHolderId(Long policyHolderId) {
        this.policyHolderId = policyHolderId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InsuranceType getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getInsuredAmount() {
        return insuredAmount;
    }

    public void setInsuredAmount(BigDecimal insuredAmount) {
        this.insuredAmount = insuredAmount;
    }

    public String getInsuredPersonFirstName() {
        return insuredPersonFirstName;
    }

    public void setInsuredPersonFirstName(String insuredPersonFirstName) {
        this.insuredPersonFirstName = insuredPersonFirstName;
    }

    public String getInsuredPersonLastName() {
        return insuredPersonLastName;
    }

    public void setInsuredPersonLastName(String insuredPersonLastName) {
        this.insuredPersonLastName = insuredPersonLastName;
    }

    public String getPolicyHolderFirstName() {
        return policyHolderFirstName;
    }

    public void setPolicyHolderFirstName(String policyHolderFirstName) {
        this.policyHolderFirstName = policyHolderFirstName;
    }

    public String getPolicyHolderLastName() {
        return policyHolderLastName;
    }

    public void setPolicyHolderLastName(String policyHolderLastName) {
        this.policyHolderLastName = policyHolderLastName;
    }
}
