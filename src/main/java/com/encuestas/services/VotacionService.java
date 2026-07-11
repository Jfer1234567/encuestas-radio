package com.encuestas.services;

import com.encuestas.models.Encuesta;
import com.encuestas.models.Opcion;
import com.encuestas.models.Voto;
import com.encuestas.repositories.EncuestaRepository;
import com.encuestas.repositories.OpcionRepository;
import com.encuestas.repositories.VotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VotacionService {

    @Autowired
    private EncuestaRepository encuestaRepository;

    @Autowired
    private OpcionRepository opcionRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional
    public boolean registrarVoto(Long opcionId, String ipUsuario) {
        Opcion opcion = opcionRepository.findById(opcionId)
                .orElseThrow(() -> new RuntimeException("La opción seleccionada no existe"));

        Encuesta encuesta = opcion.getEncuesta();

        // Validamos si la encuesta fue cerrada por el administrador
        if (!encuesta.isActiva() || votoRepository.existsByIpUsuarioAndOpcion_Encuesta_Id(ipUsuario, encuesta.getId())) {
            return false;
        }

        Voto nuevoVoto = new Voto();
        nuevoVoto.setIpUsuario(ipUsuario);
        nuevoVoto.setOpcion(opcion);
        votoRepository.save(nuevoVoto);

        opcion.setCantidadVotos(opcion.getCantidadVotos() + 1);
        opcionRepository.save(opcion);

        List<Opcion> resultadosActualizados = opcionRepository.findByEncuestaId(encuesta.getId());
        messagingTemplate.convertAndSend("/topic/resultados", resultadosActualizados);

        return true;
    }

    @Transactional
    public void eliminarEncuestaCompleta(Long encuestaId) {
        votoRepository.deleteByOpcion_Encuesta_Id(encuestaId);
        opcionRepository.deleteByEncuestaId(encuestaId);
        encuestaRepository.deleteById(encuestaId);
    }

    @Transactional
    public void reiniciarVotos(Long encuestaId) {
        votoRepository.deleteByOpcion_Encuesta_Id(encuestaId);
        opcionRepository.resetearContadoresPorEncuesta(encuestaId);

        List<Opcion> opcionesEnCero = opcionRepository.findByEncuestaId(encuestaId);
        messagingTemplate.convertAndSend("/topic/resultados", opcionesEnCero);
    }

    // Nuevo método para pausar o reabrir una votación
    @Transactional
    public void cambiarEstadoEncuesta(Long encuestaId) {
        Encuesta encuesta = encuestaRepository.findById(encuestaId)
                .orElseThrow(() -> new RuntimeException("Encuesta no encontrada"));

        encuesta.setActiva(!encuesta.isActiva());
        encuestaRepository.save(encuesta);
    }
}