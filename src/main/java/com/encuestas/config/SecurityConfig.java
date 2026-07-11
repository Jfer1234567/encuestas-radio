package com.tuproyecto.encuestas.config; // <-- ¡Cámbialo por tu package real!

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Reglas de protección de rutas
                .authorizeHttpRequests(auth -> auth
                        // Permite que carguen los estilos e imágenes en la pantalla de login
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**").permitAll()
                        // Exige login para TODO lo demás (incluido el index)
                        .anyRequest().authenticated()
                )
                // 2. Configuración de la pantalla de login
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true) // Redirige al index después de loguearse con éxito
                        .permitAll()
                )
                // 3. Configuración del botón de cerrar sesión
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}