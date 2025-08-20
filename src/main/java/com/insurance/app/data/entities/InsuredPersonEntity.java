package com.insurance.app.data.entities;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity representing an insured person.
 */
@Entity
public class InsuredPersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private int age;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;
    private String street;
    private String postalCode;
    private String city;

    @OneToMany(mappedBy = "insuredPerson", cascade = CascadeType.ALL)
    private List<InsuranceEntity> insurances;

    // --- Getters and Setters ---

    /**
     * Returns the ID of the insured person.
     */
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    /**
     * Returns the first name of the insured person.
     */
    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Returns the last name of the insured person.
     */
    public String getLastName() { return lastName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Returns the age of the insured person.
     */
    public int getAge() { return age; }

    public void setAge(int age) { this.age = age; }

    /**
     * Returns the email address of the insured person.
     */
    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    /**
     * Returns the street address of the insured person.
     */
    public String getStreet() { return street; }

    public void setStreet(String street) { this.street = street; }

    /**
     * Returns the city of the insured person.
     */
    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }

    /**
     * Returns the postal code of the insured person.
     */
    public String getPostalCode() { return postalCode; }

    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    /**
     * Returns the phone number of the insured person.
     */
    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Returns the list of insurance policies associated with this insured person.
     */
    public List<InsuranceEntity> getInsurances() { return insurances; }

    public void setInsurances(List<InsuranceEntity> insurances) { this.insurances = insurances; }
}
