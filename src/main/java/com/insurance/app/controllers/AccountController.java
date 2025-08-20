package com.insurance.app.controllers;

import com.insurance.app.models.dto.InsuredPersonDTO;
import com.insurance.app.models.dto.UserDTO;
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

    /**
     * Displays the registration page.
     *
     * @param model The model to populate form data.
     * @return The registration page view.
     */
    @GetMapping("/register")
    public String renderRegister(Model model) {
        if (!model.containsAttribute("userDTO")) {
            model.addAttribute("userDTO", new UserDTO());
        }
        return "pages/account/register";
    }

    /**
     * Handles user registration.
     *
     * @param userDTO The user registration data.
     * @param result Binding result for validation errors.
     * @param redirectAttributes Redirect attributes to pass data between redirects.
     * @return Redirect to registration or login page based on result.
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userDTO") UserDTO userDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        // Handle validation errors
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDTO", result);
            redirectAttributes.addFlashAttribute("userDTO", userDTO);
            return "redirect:/account/register";
        }

        try {
            // Attempt to create a new user
            userService.create(userDTO, false);
            // Vytvorenie poistenca s predvolenými údajmi po registrácii používateľa
            InsuredPersonDTO insuredPerson = new InsuredPersonDTO();
            insuredPerson.setFirstName(userDTO.getFirstName());
            insuredPerson.setLastName(userDTO.getLastName());
            insuredPerson.setEmail(userDTO.getEmail());

            insuredPersonService.create(insuredPerson);

        } catch (DuplicateEmailException e) {
            // Handle duplicate email error
            result.rejectValue("email", "error", "E-mail sa už používa.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDTO", result);
            redirectAttributes.addFlashAttribute("userDTO", userDTO);
            return "redirect:/account/register";
        } catch (PasswordsDoNotEqualException e) {
            // Handle passwords mismatch error
            result.rejectValue("password", "error", "Heslá sa nezhodujú.");
            result.rejectValue("confirmPassword", "error", "Heslá sa nezhodujú.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userDTO", result);
            redirectAttributes.addFlashAttribute("userDTO", userDTO);
            return "redirect:/account/register";
        }

        // Registration successful
        redirectAttributes.addFlashAttribute("success", "Používateľ zaregistrovaný.");
        return "redirect:/account/login";
    }
}
