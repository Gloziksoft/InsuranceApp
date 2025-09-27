package com.insurance.app.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

/**
 * Data Transfer Object for insured person data.
 */
public class InsuredPersonDTO {

    private Long id; // insured person ID

    @NotBlank(message = "Meno je povinné")
    private String firstName; // first name

    @NotBlank(message = "Priezvisko je povinné")
    private String lastName; // last name

    @NotNull(message = "Je potrebné uviesť vek.")
    @PositiveOrZero(message = "Vek musí byť 0 alebo vyšší.")
    private Double age; // age

    @Email(message = "Zadajte platný email")
    @NotBlank(message = "Email je povinný")
    private String email; // email address

    @NotBlank(message = "Telefón je povinný")
    private String phone; // phone number

    @NotBlank(message = "Ulica je povinná")
    private String street; // street address

    @NotBlank(message = "Mesto je povinné")
    private String city; // city

    @NotBlank(message = "PSČ je povinné")
    private String postalCode; // postal code

    private List<InsuranceDTO> insurances; // list of insurances

    // Getters and setters

    /**
     * Gets the insured person ID.
     */
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    /**
     * Gets the first name.
     */
    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Gets the last name.
     */
    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Gets the age.
     */
    public Double getAge() {
        return age;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    /**
     * Gets the email address.
     */
    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    /**
     * Gets the phone number.
     */
    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Gets the street address.
     */
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gets the city.
     */
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the postal code.
     */
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the list of associated insurances.
     */
    public List<InsuranceDTO> getInsurances() { return insurances; }

    public void setInsurances(List<InsuranceDTO> insurances) { this.insurances = insurances; }
}
