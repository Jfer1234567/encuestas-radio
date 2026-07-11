package com.encuestas.repositories;

import com.encuestas.models.Encuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncuestaRepository extends JpaRepository<Encuesta, Long> {
    // Al extender de JpaRepository, Spring ya nos regala métodos como:
    // save(), findAll(), findById(), deleteById()
}