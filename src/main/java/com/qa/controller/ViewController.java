package com.qa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/ui/dashboard")
    public String getDashboard() {
        return "dashboard";
    }

    @GetMapping("/ui/login")
    public String showLoginPage() {
        return "login";
    }

    // ADD THIS METHOD BELOW
    @GetMapping("/ui/verify")
    public String showVerifyPage() {
        return "verify";
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/ui/login";
    }

    @GetMapping("/ui/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }
}