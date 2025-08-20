package com.insurance.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling general application pages like home and about us.
 */
@Controller
public class HomeController {

    /**
     * Renders the home page.
     *
     * @return The home page view.
     */
    @GetMapping("/")
    public String renderIndex() {
        return "pages/home/index";
    }

    /**
     * Renders the About Us page.
     *
     * @return The About Us page view.
     */
    @GetMapping("/about-us")
    public String renderAboutUs() {
        return "pages/home/about-us";
    }
}
