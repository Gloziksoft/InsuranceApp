package com.insurance.app.controllers;

import com.insurance.app.models.dto.EventDTO;
import com.insurance.app.models.dto.InsuranceDTO;
import com.insurance.app.models.services.EventService;
import com.insurance.app.models.services.InsuranceService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.OutputStream;
import java.util.List;

@Controller
@RequestMapping("/insurance/{insuranceId}/events")
public class EventReportsController {

    private final EventService eventService;
    private final InsuranceService insuranceService;
    private final TemplateEngine templateEngine;

    @Autowired
    public EventReportsController(
            EventService eventService,
            InsuranceService insuranceService,
            TemplateEngine templateEngine) {
        this.eventService = eventService;
        this.insuranceService = insuranceService;
        this.templateEngine = templateEngine;
    }

    @GetMapping
    public String listEvents(@PathVariable Long insuranceId, Model model) {
        InsuranceDTO insurance = insuranceService.findById(insuranceId);
        if (insurance == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poistenie nenájdené");
        }

        List<EventDTO> events = eventService.findByInsuranceId(insuranceId);

        model.addAttribute("insurance", insurance);
        model.addAttribute("events", events);
        return "pages/events/index";
    }

    // HTML report
    @GetMapping("/reports")
    public String showReport(@PathVariable Long insuranceId, Model model) {
        List<EventDTO> events;
        InsuranceDTO insurance = null;

        if (insuranceId == 0) {
            events = eventService.findAll();
        } else {
            insurance = insuranceService.findById(insuranceId);

            if (insurance == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poistenie s ID " + insuranceId + " neexistuje.");
            }

            events = eventService.findByInsuranceId(insuranceId);
        }

        model.addAttribute("insurance", insurance);
        model.addAttribute("events", events);
        return "pages/events/reports";
    }



    @PostMapping("/events/clean")
    public String cleanInvalidEvents(RedirectAttributes redirectAttributes) {
        eventService.deleteEventsWithInvalidDescriptions();
        redirectAttributes.addFlashAttribute("success", "Neplatné udalosti boli vymazané.");
        return "redirect:/insurance";
    }


    // Export do PDF
    @GetMapping("/reports/export")
    public void exportReportToPDF(@PathVariable Long insuranceId, HttpServletResponse response) {
        try {
            InsuranceDTO insurance = null;
            List<EventDTO> events;

            if (insuranceId == 0) {
                events = eventService.findAll(); // všetky udalosti
            } else {
                insurance = insuranceService.findById(insuranceId);
                if (insurance == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Poistenie s ID " + insuranceId + " neexistuje.");
                }
                events = eventService.findByInsuranceId(insuranceId);
            }

            Context context = new Context();
            context.setVariable("insurance", insurance);
            context.setVariable("events", events);

            String html = templateEngine.process("pages/events/reports-pdf", context);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

            try (OutputStream out = response.getOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(out);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Chyba pri exporte PDF.", e);
        }
    }
}
