package com.encuestas;

import com.encuestas.models.Usuario;
import com.encuestas.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class EncuestasApplication {

    public static void main(String[] args) {
        SpringApplication.run(EncuestasApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername("fernando");

            if (usuarioOpt.isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("fernando");
                admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña nueva
                admin.setRole("ADMIN");
                usuarioRepository.save(admin);
                System.out.println("Usuario 'fernando' creado con contraseña: admin123");
            } else {
                // Si ya existe, forzamos la actualización de la contraseña por si se quedó bloqueada
                Usuario admin = usuarioOpt.get();
                admin.setPassword(passwordEncoder.encode("admin123"));
                usuarioRepository.save(admin);
                System.out.println("Contraseña del usuario 'fernando' actualizada forzosamente a: admin123");
            }
        };
    }
}