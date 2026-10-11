package com.insurance.app.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Data Transfer Object for insured person data.
 */
public class InsuredPersonDTO {

    private Long id; // insured person ID

    @NotBlank(message = "Meno je povinné")
    @Size(max = 50, message = "Meno môže mať maximálne 50 znakov")
    @Pattern(
            regexp = "^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
            message = "Meno obsahuje nepovolené znaky"
    )
    private String firstName;

    @NotBlank(message = "Priezvisko je povinné")
    @Size(max = 50, message = "Priezvisko môže mať maximálne 50 znakov")
    @Pattern(
            regexp = "^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
            message = "Priezvisko obsahuje nepovolené znaky"
    )
    private String lastName;

    @NotNull(message = "Je potrebné uviesť vek.")
    @Min(value = 0, message = "Vek nemôže byť záporný")
    @Max(value = 120, message = "Vek musí byť najviac 120 rokov")
    private Integer age;

    @NotBlank(message = "Email je povinný")
    @Email(message = "Zadajte platný email")
    @Size(max = 254, message = "Email môže mať maximálne 254 znakov")
    private String email;

    @NotBlank(message = "Telefón je povinný")
    @Size(max = 20, message = "Telefón môže mať maximálne 20 znakov")
    @Pattern(
            regexp = "^\\+?[0-9][0-9 ()/\\-]{6,18}[0-9]$",
            message = "Zadajte platné telefónne číslo"
    )
    private String phone;

    @NotBlank(message = "Ulica je povinná")
    @Size(max = 100, message = "Ulica môže mať maximálne 100 znakov")
    @Pattern(
            regexp = "^[\\p{L}\\p{N} .,'’/\\-]+$",
            message = "Ulica obsahuje nepovolené znaky"
    )
    private String street;

    @NotBlank(message = "Mesto je povinné")
    @Size(max = 100, message = "Mesto môže mať maximálne 100 znakov")
    @Pattern(
            regexp = "^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
            message = "Mesto obsahuje nepovolené znaky"
    )
    private String city;

    @NotBlank(message = "PSČ je povinné")
    @Pattern(
            regexp = "^\\d{3}\\s?\\d{2}$",
            message = "PSČ musí mať formát 12345 alebo 123 45"
    )
    private String postalCode;

    private List<InsuranceDTO> insurances;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public List<InsuranceDTO> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<InsuranceDTO> insurances) {
        this.insurances = insurances;
    }
}