package com.encuestas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. RUTAS PÚBLICAS (Quitamos la "/" de aquí)
                        .requestMatchers("/dashboard", "/api/votar", "/ws-encuestas/**", "/css/**", "/js/**", "/img/**").permitAll()

                        // 2. RUTAS PROTEGIDAS (Obligamos a que la raíz y el admin pidan login)
                        .requestMatchers("/").authenticated()
                        .requestMatchers("/admin", "/admin/**").authenticated()

                        // Cualquier otra ruta no especificada también requerirá login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin", true) // Te manda a /admin tras loguearte
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}