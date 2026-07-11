package com.encuestas.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Esto buscará automáticamente el archivo 'login.html' en tu carpeta templates
    }
}