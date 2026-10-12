package com.insurance.app.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordDTO {

    private String token;

    @NotBlank(message = "Heslo je povinné.")
    @Size(min = 8, max = 72, message = "Heslo musí mať 8 až 72 znakov.")
    private String password;

    @NotBlank(message = "Potvrdenie hesla je povinné.")
    @Size(min = 8, max = 72, message = "Potvrdené heslo musí mať 8 až 72 znakov.")
    private String confirmPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
}