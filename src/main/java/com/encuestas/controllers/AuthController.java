package com.encuestas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin() {
        // Redirige al archivo login.html
        return "login";
    }
}