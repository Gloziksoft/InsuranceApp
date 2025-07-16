package com.poistenie.app.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration data.
 */
public class UserDTO {

    @Email(message = "Zadajte platný e-mail.")
    @NotBlank(message = "E-mail je povinný.")
    private String email;

    @NotBlank(message = "Heslo je povinné.")
    @Size(min = 3, message = "Heslo musí mať aspoň 6 znakov.")
    private String password;

    @NotBlank(message = "Potvrdenie hesla je povinné.")
    @Size(min = 3, message = "Potvrdené heslo musí mať aspoň 6 znakov.")
    private String confirmPassword;

    @NotBlank(message = "Meno je povinné.")
    private String firstName;

    @NotBlank(message = "Priezvisko je povinné.")
    private String lastName;

    // region: getters and setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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

    // endregion
}
