package com.poistenie.app.controllers;

import com.poistenie.app.data.entities.UserEntity;
import com.poistenie.app.models.dto.InsuranceDTO;
import com.poistenie.app.models.dto.InsuredPersonDTO;
import com.poistenie.app.models.services.InsuranceService;
import com.poistenie.app.models.services.InsuredPersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

/**
 * Controller for managing insurance policies - list, create, edit, delete operations.
 */
@Controller
@RequestMapping("/insurance")
public class InsuranceController {

    private final InsuredPersonService insuredPersonService;
    private final InsuranceService insuranceService;

    @Autowired
    public InsuranceController(InsuranceService insuranceService,
                               InsuredPersonService insuredPersonService) {
        this.insuranceService = insuranceService;
        this.insuredPersonService = insuredPersonService;
    }

    /**
     * Displays a paginated list of insurance policies with optional search.
     */
    @GetMapping
    public String listAll(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "3") int size,
                          @RequestParam(required = false) String keyword,
                          Model model,
                          Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size);
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        Page<InsuranceDTO> insurancePage;

        if (currentUser.isAdmin()) {
            // Admin vidí všetko, alebo môže vyhľadávať
            insurancePage = (keyword != null && !keyword.isEmpty())
                    ? insuranceService.search(keyword, pageable)
                    : insuranceService.findAll(pageable);
        } else {
            // Bežný používateľ vidí iba svoje poistenia
            insurancePage = insuranceService.findByUserEmail(currentUser.getEmail(), pageable);
        }

        model.addAttribute("insurances", insurancePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", insurancePage.getTotalPages());
        model.addAttribute("keyword", keyword);
        return "pages/insurance/index";
    }

    /**
     * Displays detailed view of a specific insurance policy.
     */
    @GetMapping("/detail/{id}")
    public String insuranceDetail(@PathVariable Long id, Model model, Authentication authentication) {
        InsuranceDTO insurance = insuranceService.findById(id);
        InsuredPersonDTO insuredPerson = insuredPersonService.findById(insurance.getInsuredPersonId());

        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        // Admin môže všetko, používateľ môže iba ak ide o jeho poistenie
        if (!currentUser.isAdmin() && !currentUser.getEmail().equals(insuredPerson.getEmail())) {
            throw new AccessDeniedException("Nemáte oprávnenie zobraziť toto poistenie.");
        }

        InsuredPersonDTO policyHolder = null;
        if (insurance.getPolicyHolderId() != null) {
            policyHolder = insuredPersonService.findById(insurance.getPolicyHolderId());
        }

        model.addAttribute("insurance", insurance);
        model.addAttribute("insuredPerson", insuredPerson);
        model.addAttribute("policyHolder", policyHolder);
        return "pages/insurance/detail";
    }


    /**
     * Displays form to create a new insurance policy (admin only).
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("insurance", new InsuranceDTO());
        model.addAttribute("persons", insuredPersonService.findAll());
        return "pages/insurance/create";
    }

    /**
     * Displays form to create a new insurance policy for a specific person (admin only).
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/create/{personId}")
    public String createFormWithPerson(@PathVariable Long personId, Model model) {
        InsuranceDTO insurance = new InsuranceDTO();
        insurance.setInsuredPersonId(personId);
        model.addAttribute("insurance", insurance);
        model.addAttribute("persons", insuredPersonService.findAll());
        return "pages/insurance/create";
    }

    /**
     * Handles creation of a new insurance policy (admin only).
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("insurance") InsuranceDTO dto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("persons", insuredPersonService.findAll());
            return "pages/insurance/create";
        }

        insuranceService.create(dto);

        redirectAttributes.addFlashAttribute("success", "Poistenie bolo úspešne vytvorené.");
        return "redirect:/insurance";
    }

    /**
     * Displays form to edit an insurance policy (admin only).
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("insurance", insuranceService.findById(id));
        model.addAttribute("persons", insuredPersonService.findAll());
        return "pages/insurance/edit";
    }

    /**
     * Handles updating an insurance policy (admin only).
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("insurance") InsuranceDTO dto,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("persons", insuredPersonService.findAll());
            return "pages/insurance/edit";
        }

        insuranceService.update(id, dto);

        redirectAttributes.addFlashAttribute("success", "Poistenie bolo úspešne aktualizované.");
        return "redirect:/insurance";
    }

    /**
     * Handles deletion of an insurance policy (admin only).
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        insuranceService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Poistenie bolo úspešne odstránené.");
        return "redirect:/insurance";
    }

    /**
     * Handles cases where insurance is not found.
     */
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNotFound(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Poistenie nebolo nájdené.");
        return "redirect:/insurance";
    }
}
