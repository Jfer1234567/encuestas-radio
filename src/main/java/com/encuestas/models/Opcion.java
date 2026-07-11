package com.encuestas.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "opciones")
public class Opcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texto; // Ej: "Lista A", "Lista B", "Voto Blanco"

    private int cantidadVotos = 0; // Guardamos el total aquí para que el dashboard cargue rapidísimo

    @ManyToOne
    @JoinColumn(name = "encuesta_id")
    @JsonIgnore
    private Encuesta encuesta;
}