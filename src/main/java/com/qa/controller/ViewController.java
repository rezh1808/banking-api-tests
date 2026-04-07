package com.qa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Note: Use @Controller, NOT @RestController
public class ViewController {

    // This matches the variable in your AuthController
    public static int mockBalance = 1000;

    @GetMapping("/ui/dashboard")
    public String getDashboard(Model model) {
        // This sends "1000" to the HTML page
        model.addAttribute("balance", AuthController.mockBalance);
        return "dashboard"; // This must match the filename dashboard.html
    }

    @GetMapping("/ui/login")
    public String showLoginPage() {
        return "login"; // Looks for login.html
    }

    // Also redirect the root "/" to login
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/ui/login";
    }
}