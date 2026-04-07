package com.qa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Note: Use @Controller, NOT @RestController
public class ViewController {

    // This matches the variable in your AuthController
    private int mockBalance = 1000;

    @GetMapping("/ui/dashboard")
    public String getDashboard(Model model) {
        // This sends "1000" to the HTML page
        model.addAttribute("balance", mockBalance);
        return "dashboard"; // This must match the filename dashboard.html
    }
}