package com.encuestas.controllers;

import com.encuestas.models.Encuesta;
import com.encuestas.models.Opcion;
import com.encuestas.repositories.EncuestaRepository;
import com.encuestas.repositories.OpcionRepository;
import com.encuestas.services.VotacionService;
import com.encuestas.repositories.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class VotacionController {

    @Autowired
    private EncuestaRepository encuestaRepository;

    @Autowired
    private OpcionRepository opcionRepository;

    @Autowired
    private VotacionService votacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/")
    public String mostrarPaginaVotacion(Model model) {
        model.addAttribute("encuestas", encuestaRepository.findAll());
        return "index";
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        model.addAttribute("encuestas", encuestaRepository.findAll());
        return "dashboard";
    }

    @PostMapping("/api/votar")
    @ResponseBody
    public String recibirVoto(@RequestParam Long opcionId, HttpServletRequest request) {
        String ipUsuario = request.getRemoteAddr();
        boolean exito = votacionService.registrarVoto(opcionId, ipUsuario);

        if (exito) {
            return "¡Éxito! Tu voto ha sido registrado correctamente.";
        } else {
            return "Error: Ya registramos un voto desde tu conexión o la encuesta cerró.";
        }
    }

    @GetMapping("/admin")
    public String mostrarAdmin(Model model,
                               @RequestParam(required = false) String exito,
                               @RequestParam(required = false) String eliminado,
                               @RequestParam(required = false) String reiniciado) {

        if (exito != null) {
            model.addAttribute("mensajeVerde", "¡Encuesta creada y publicada en vivo!");
        }

        if (eliminado != null) {
            model.addAttribute("mensajeRojo", "Encuesta eliminada correctamente del sistema.");
        }

        if (reiniciado != null) {
            model.addAttribute("mensajeAmarillo", "¡Los votos han sido reiniciados a cero exitosamente!");
        }

        // Enviamos las encuestas a la vista
        model.addAttribute("encuestas", encuestaRepository.findAll());

        // Enviamos la lista de usuarios (locutores)
        model.addAttribute("usuarios", usuarioRepository.findAll());

        return "admin";
    }

    @PostMapping("/admin/crear")
    public String crearEncuesta(@RequestParam String titulo, @RequestParam List<String> opcionesForm) {
        Encuesta nuevaEncuesta = new Encuesta();
        nuevaEncuesta.setTitulo(titulo);
        encuestaRepository.save(nuevaEncuesta);

        for (String textoOpcion : opcionesForm) {
            if (textoOpcion != null && !textoOpcion.trim().isEmpty()) {
                Opcion nuevaOpcion = new Opcion();
                nuevaOpcion.setTexto(textoOpcion);
                nuevaOpcion.setEncuesta(nuevaEncuesta);
                opcionRepository.save(nuevaOpcion);
            }
        }
        return "redirect:/admin?exito=true";
    }

    @PostMapping("/admin/eliminar")
    public String eliminarEncuesta(@RequestParam Long encuestaId) {
        votacionService.eliminarEncuestaCompleta(encuestaId);
        return "redirect:/admin?eliminado=true";
    }

    @PostMapping("/admin/reiniciar")
    public String reiniciarVotos(@RequestParam Long encuestaId) {
        votacionService.reiniciarVotos(encuestaId);
        return "redirect:/admin?reiniciado=true";
    }

    // Nuevo Endpoint para el botón de Pausar / Cerrar
    @PostMapping("/admin/estado")
    public String cambiarEstado(@RequestParam Long encuestaId) {
        votacionService.cambiarEstadoEncuesta(encuestaId);
        return "redirect:/admin";
    }
}