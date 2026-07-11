package com.encuestas.controllers;

import com.encuestas.models.Usuario;
import com.encuestas.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/admin/locutores/crear")
    public String crearLocutor(@RequestParam String username, @RequestParam String password) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            return "redirect:/admin?errorUsuario=true";
        }
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setPassword(passwordEncoder.encode(password));
        nuevoUsuario.setRole("ADMIN");
        usuarioRepository.save(nuevoUsuario);
        return "redirect:/admin?exitoUsuario=true";
    }

    // NUEVO: Eliminar Locutor
    @PostMapping("/admin/locutores/eliminar")
    public String eliminarLocutor(@RequestParam Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/admin?eliminadoUsuario=true";
    }

    // NUEVO: Editar Locutor
    @PostMapping("/admin/locutores/editar")
    public String editarLocutor(@RequestParam Long id, @RequestParam String username, @RequestParam(required = false) String password) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setUsername(username);

        // Si escribió una nueva contraseña, se encripta y actualiza. Si lo dejó vacío, conserva la vieja.
        if (password != null && !password.trim().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(password));
        }

        usuarioRepository.save(usuario);
        return "redirect:/admin?editadoUsuario=true";
    }
}