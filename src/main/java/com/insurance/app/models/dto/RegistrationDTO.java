package com.insurance.app.models.dto;

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
    @Pattern(
            regexp = "^$|^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
            message = "Meno obsahuje nepovolené znaky."
    )
    private String firstName;

    @NotBlank(message = "Priezvisko musí byť zadané.")
    @Size(max = 50, message = "Priezvisko môže mať maximálne 50 znakov.")
    @Pattern(
            regexp = "^$|^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$",
            message = "Priezvisko obsahuje nepovolené znaky."
    )
    private String lastName;


    @NotBlank(message = "Email musí byť zadaný.")
    @Pattern(
            regexp = "^(?:\\s*|[A-Za-z0-9_%+-]+(?:\\.[A-Za-z0-9_%+-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+[A-Za-z]{2,63})$",
            message = "Zadajte platnú e-mailovú adresu."
    )
    @Size(max = 254, message = "Email môže mať maximálne 254 znakov.")
    private String email;


    @NotBlank(message = "Heslo je povinné.")
    @Size(min = 8, max = 72, message = "Heslo musí mať 8 až 72 znakov.")
    private String password;

    @NotBlank(message = "Potvrdenie hesla je povinné.")
    @Size(min = 8, max = 72, message = "Potvrdené heslo musí mať 8 až 72 znakov.")
    private String confirmPassword;
}