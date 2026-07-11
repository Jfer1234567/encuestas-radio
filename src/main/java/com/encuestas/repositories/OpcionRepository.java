package com.encuestas.repositories;

import com.encuestas.models.Opcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OpcionRepository extends JpaRepository<Opcion, Long> {

    // Busca las opciones de una encuesta
    List<Opcion> findByEncuestaId(Long encuestaId);

    // Permite borrar las opciones de una encuesta
    @Transactional
    void deleteByEncuestaId(Long encuestaId);

    // NUEVO: Permite reiniciar contadores a 0
    @Modifying
    @Transactional
    @Query("UPDATE Opcion o SET o.cantidadVotos = 0 WHERE o.encuesta.id = :encuestaId")
    void resetearContadoresPorEncuesta(Long encuestaId);
}