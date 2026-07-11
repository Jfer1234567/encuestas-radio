package com.encuestas.repositories;

import com.encuestas.models.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    // Verifica si un usuario ya votó
    boolean existsByIpUsuarioAndOpcion_Encuesta_Id(String ipUsuario, Long encuestaId);

    // Permite borrar los votos asociados a una encuesta
    @Transactional
    void deleteByOpcion_Encuesta_Id(Long encuestaId);

    // Permite obtener todos los votos de una encuesta para los reportes
    java.util.List<com.encuestas.models.Voto> findByOpcion_Encuesta_Id(Long encuestaId);
}