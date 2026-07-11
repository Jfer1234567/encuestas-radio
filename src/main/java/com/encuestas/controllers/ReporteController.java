package com.encuestas.controllers;

import com.encuestas.services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/excel/{encuestaId}")
    public void descargarExcel(@PathVariable Long encuestaId, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=Votacion_" + encuestaId + ".xlsx");
        reporteService.exportarExcel(encuestaId, response);
    }

    @GetMapping("/pdf/{encuestaId}")
    public void descargarPdf(@PathVariable Long encuestaId, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Votacion_" + encuestaId + ".pdf");
        reporteService.exportarPdf(encuestaId, response);
    }
}