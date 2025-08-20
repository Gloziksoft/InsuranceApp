package com.insurance.app.controllers;

import com.insurance.app.data.entities.UserEntity;
import com.insurance.app.models.dto.InsuredPersonDTO;
import com.insurance.app.models.services.InsuredPersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

/**
 * Controller for managing insured persons - list, create, edit, delete operations.
 */
@Controller
@RequestMapping("/insured-persons")
public class InsuredPersonController {

    private final InsuredPersonService insuredPersonService;

    @Autowired
    public InsuredPersonController(InsuredPersonService insuredPersonService) {
        this.insuredPersonService = insuredPersonService;
    }

    /**
     * Displays a paginated list of insured persons with optional search.
     */
    @GetMapping
    public String listPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(required = false) String search,
            Model model,
            Authentication authentication
    ) {
        Pageable pageable = PageRequest.of(page, size);
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        Page<InsuredPersonDTO> resultPage;

        if (currentUser.isAdmin()) {
            // Admin vidí všetko, prípadne filtrované podľa search
            resultPage = (search != null && !search.isBlank())
                    ? insuredPersonService.searchByName(search, pageable)
                    : insuredPersonService.getAllPaginated(pageable);
        } else {
            // Bežný používateľ vidí iba svoje záznamy (podľa emailu)
            resultPage = insuredPersonService.findByEmail(currentUser.getEmail(), pageable);
        }

        model.addAttribute("insuredPersons", resultPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", resultPage.getTotalPages());
        model.addAttribute("search", search);

        return "pages/insuredPersons/index";
    }

    /**
     * Displays detail view of a specific insured person.
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication authentication) {
        InsuredPersonDTO insuredPerson = insuredPersonService.findById(id);
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        // Kontrola oprávnenia – ADMIN alebo vlastný záznam
        if (!currentUser.canModify(insuredPerson.getEmail())) {
            throw new AccessDeniedException("Nemáte oprávnenie zobraziť tento záznam.");
        }

        model.addAttribute("insuredPerson", insuredPerson);
        return "pages/insuredPersons/detail";
    }


    /**
     * Displays form for creating a new insured person (admin only).
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("insuredPerson", new InsuredPersonDTO());
        return "pages/insuredPersons/create";
    }

    /**
     * Handles creation of a new insured person (admin only).
     */
    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("insuredPerson") InsuredPersonDTO insuredPerson,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "pages/insuredPersons/create";
        }
        insuredPersonService.create(insuredPerson);
        redirectAttributes.addFlashAttribute("success", "Poistená osoba bola úspešne vytvorená.");
        return "redirect:/insured-persons";
    }

    /**
     * Displays form for editing an insured person (admin or owner).
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, Authentication authentication) {
        InsuredPersonDTO insuredPerson = insuredPersonService.findById(id);
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        if (!currentUser.canModify(insuredPerson.getEmail())) {
            throw new AccessDeniedException("Nemáte oprávnenie upravovať tento záznam.");
        }

        model.addAttribute("insuredPerson", insuredPerson);
        return "pages/insuredPersons/edit";
    }

    /**
     * Handles updating an insured person (admin or owner).
     */
    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("insuredPerson") InsuredPersonDTO insuredPerson,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Authentication authentication
    ) {
        if (result.hasErrors()) {
            return "pages/insuredPersons/edit";
        }

        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        // Zisti aktuálny email z DB
        InsuredPersonDTO existingPerson = insuredPersonService.findById(id);

        if (!currentUser.canModify(existingPerson.getEmail())) {
            throw new AccessDeniedException("Nemáte oprávnenie upravovať tento záznam.");
        }

        insuredPersonService.update(id, insuredPerson);
        redirectAttributes.addFlashAttribute("success", "Poistená osoba bola úspešne aktualizovaná.");
        return "redirect:/insured-persons";
    }

    /**
     * Handles deleting an insured person (admin or owner).
     */
    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Authentication authentication
    ) {
        InsuredPersonDTO insuredPerson = insuredPersonService.findById(id);
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        if (!currentUser.canModify(insuredPerson.getEmail())) {
            throw new AccessDeniedException("Nemáte oprávnenie odstrániť tento záznam.");
        }

        insuredPersonService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Poistená osoba bola úspešne odstránená.");
        return "redirect:/insured-persons";
    }

    /**
     * Handles cases where insured person is not found.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNotFound(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Poistená osoba nebola nájdená.");
        return "redirect:/insured-persons";
    }
}
