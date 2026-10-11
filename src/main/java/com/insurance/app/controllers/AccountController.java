package com.insurance.app.controllers;

import com.insurance.app.models.dto.InsuredPersonDTO;
import com.insurance.app.models.dto.RegistrationDTO;
import com.insurance.app.models.exceptions.DuplicateEmailException;
import com.insurance.app.models.exceptions.PasswordsDoNotEqualException;
import com.insurance.app.models.services.InsuredPersonService;
import com.insurance.app.models.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.insurance.app.models.dto.ForgotPasswordDTO;
import com.insurance.app.models.dto.ResetPasswordDTO;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling user account actions like login and registration.
 */
@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private InsuredPersonService insuredPersonService;

    /**
     * Displays the login page.
     *
     * @return The login page view.
     */
    @GetMapping("login")
    public String renderLogin() {
        return "pages/account/login.html";
    }

    @GetMapping("/forgot-password")
    public String renderForgotPassword(Model model) {
        if (!model.containsAttribute("forgotPasswordDTO")) {
            model.addAttribute("forgotPasswordDTO", new ForgotPasswordDTO());
        }
        return "pages/account/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @Valid @ModelAttribute("forgotPasswordDTO") ForgotPasswordDTO forgotPasswordDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.forgotPasswordDTO",
                    result
            );
            redirectAttributes.addFlashAttribute("forgotPasswordDTO", forgotPasswordDTO);
            return "redirect:/account/forgot-password";
        }

        userService.forgotPassword(forgotPasswordDTO.getEmail());

        redirectAttributes.addFlashAttribute(
                "success",
                "Ak účet s touto e-mailovou adresou existuje, bol odoslaný odkaz na obnovenie hesla."
        );

        return "redirect:/account/login";
    }

    @GetMapping("/reset-password")
    public String renderResetPassword(
            @RequestParam("token") String token,
            Model model
    ) {
        if (!userService.isResetTokenValid(token)) {
            return "pages/account/reset-password-error";
        }

        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setToken(token);
        model.addAttribute("resetPasswordDTO", resetPasswordDTO);

        return "pages/account/reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @Valid @ModelAttribute("resetPasswordDTO") ResetPasswordDTO resetPasswordDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "pages/account/reset-password";
        }

        boolean resetSuccessful;

        try {
            resetSuccessful = userService.resetPassword(
                    resetPasswordDTO.getToken(),
                    resetPasswordDTO.getPassword(),
                    resetPasswordDTO.getConfirmPassword()
            );
        } catch (PasswordsDoNotEqualException e) {
            result.rejectValue(
                    "password",
                    "error",
                    "Heslá sa nezhodujú."
            );
            result.rejectValue(
                    "confirmPassword",
                    "error",
                    "Heslá sa nezhodujú."
            );

            return "pages/account/reset-password";
        }

        if (!resetSuccessful) {
            return "pages/account/reset-password-error";
        }

        redirectAttributes.addFlashAttribute(
                "success",
                "Heslo bolo úspešne zmenené."
        );

        return "redirect:/account/login";
    }

    /**
     * Displays the registration page.
     *
     * @param model The model to populate form data.
     * @return The registration page view.
     */
    @GetMapping("/register")
    public String renderRegister(Model model) {
        if (!model.containsAttribute("registrationDTO")) {
            model.addAttribute("registrationDTO", new RegistrationDTO());
        }
        return "pages/account/register";
    }

    /**
     * Handles user registration.
     *
     * @param registrationDTO    The user registration data.
     * @param result             Binding result for validation errors.
     * @param redirectAttributes Redirect attributes to pass data between redirects.
     * @return Redirect to registration or login page based on result.
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registrationDTO") RegistrationDTO registrationDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        // Handle validation errors
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.registrationDTO",
                    result
            );
            redirectAttributes.addFlashAttribute("registrationDTO", registrationDTO);
            return "redirect:/account/register";
        }

        try {
            // Attempt to create a new user
            userService.create(registrationDTO, false);

            // Create insured person with default data after registration
            InsuredPersonDTO insuredPerson = new InsuredPersonDTO();
            insuredPerson.setFirstName(registrationDTO.getFirstName());
            insuredPerson.setLastName(registrationDTO.getLastName());
            insuredPerson.setEmail(registrationDTO.getEmail());

            insuredPersonService.create(insuredPerson);

        } catch (DuplicateEmailException e) {
            // Handle duplicate email error
            result.rejectValue("email", "error", "E-mail sa už používa.");
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.registrationDTO",
                    result
            );
            redirectAttributes.addFlashAttribute("registrationDTO", registrationDTO);
            return "redirect:/account/register";

        } catch (PasswordsDoNotEqualException e) {
            // Handle passwords mismatch error
            result.rejectValue("password", "error", "Heslá sa nezhodujú.");
            result.rejectValue("confirmPassword", "error", "Heslá sa nezhodujú.");
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.registrationDTO",
                    result
            );
            redirectAttributes.addFlashAttribute("registrationDTO", registrationDTO);
            return "redirect:/account/register";
        }

        // Registration successful
        redirectAttributes.addFlashAttribute("success", "Používateľ zaregistrovaný.");
        return "redirect:/account/login";
    }
}