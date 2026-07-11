package com.encuestas.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "votos")
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipUsuario; // Para detectar y bloquear si intentan votar 2 veces

    private String navegador; // Opcional: Para estadísticas (Chrome, Firefox, Celular)

    private LocalDateTime fechaHora = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "opcion_id")
    private Opcion opcion;
}