package com.insurance.app.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for user registration.
 */
@Getter
@Setter
public class RegistrationDTO {

    @NotBlank(message = "Meno musí byť zadané.")
    @Size(max = 50, message = "Meno môže mať maximálne 50 znakov.")
    private String firstName;

    @NotBlank(message = "Priezvisko musí byť zadané.")
    @Size(max = 50, message = "Priezvisko môže mať maximálne 50 znakov.")
    private String lastName;

    @NotBlank(message = "Email musí byť zadaný.")
    @Email(message = "Zadajte platnú e-mailovú adresu.")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Zadajte platnú e-mailovú adresu."
    )
    @Size(max = 254, message = "Email môže mať maximálne 254 znakov.")
    private String email;

    @NotBlank(message = "Heslo je povinné.")
    @Size(min = 8, message = "Heslo musí mať aspoň 6 znakov.")
    private String password;

    @NotBlank(message = "Potvrdenie hesla je povinné.")
    @Size(min = 8, message = "Potvrdené heslo musí mať aspoň 6 znakov.")
    private String confirmPassword;
}