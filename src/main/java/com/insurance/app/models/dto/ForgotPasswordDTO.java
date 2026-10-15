package com.insurance.app.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ForgotPasswordDTO {


    @NotBlank(message = "Email musí byť zadaný.")
    @Pattern(
            regexp = "^(?:\\s*|[A-Za-z0-9_%+-]+(?:\\.[A-Za-z0-9_%+-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+[A-Za-z]{2,63})$",
            message = "Zadajte platnú e-mailovú adresu."
    )
    @Size(max = 254, message = "Email môže mať maximálne 254 znakov.")
    private String email;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}