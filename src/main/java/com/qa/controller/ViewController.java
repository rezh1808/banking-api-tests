package com.qa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/ui/dashboard")
    public String getDashboard() {
        // We don't need "Model" anymore because JavaScript
        // will fetch the real balance from /bank/user-info
        return "dashboard";
    }

    @GetMapping("/ui/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/ui/login";
    }
}