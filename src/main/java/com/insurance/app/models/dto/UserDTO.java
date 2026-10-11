package com.insurance.app.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for User.
 * Used to transfer basic user data between application layers.
 */
@Getter
@Setter
public class UserDTO {

    @NotBlank(message = "Meno musí byť zadané.")
    @Size(max = 50, message = "Meno môže mať maximálne 50 znakov.")
    private String firstName;

    @NotBlank(message = "Priezvisko musí byť zadané.")
    @Size(max = 50, message = "Priezvisko môže mať maximálne 50 znakov.")
    private String lastName;

    @NotBlank(message = "Email musí byť zadaný.")
    @Email(message = "Zadajte platnú e-mailovú adresu.")
    @Size(max = 254, message = "Email môže mať maximálne 254 znakov.")
    private String email;
}