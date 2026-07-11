package com.encuestas.services;

import com.encuestas.models.Encuesta;
import com.encuestas.models.Voto;
import com.encuestas.repositories.EncuestaRepository;
import com.encuestas.repositories.VotoRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private EncuestaRepository encuestaRepository;

    // Exportar a Excel
    public void exportarExcel(Long encuestaId, HttpServletResponse response) throws IOException {
        Encuesta encuesta = encuestaRepository.findById(encuestaId).orElseThrow();
        List<Voto> votos = votoRepository.findByOpcion_Encuesta_Id(encuestaId);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Resultados Votación");

        // Fila de encabezados
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID Voto");
        headerRow.createCell(1).setCellValue("Candidato / Opción");
        headerRow.createCell(2).setCellValue("IP del Votante");

        // Llenar datos
        int rowIdx = 1;
        for (Voto voto : votos) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(voto.getId());
            row.createCell(1).setCellValue(voto.getOpcion().getTexto());
            row.createCell(2).setCellValue(voto.getIpUsuario());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // Exportar a PDF
    public void exportarPdf(Long encuestaId, HttpServletResponse response) throws IOException {
        Encuesta encuesta = encuestaRepository.findById(encuestaId).orElseThrow();
        List<Voto> votos = votoRepository.findByOpcion_Encuesta_Id(encuestaId);

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph titulo = new Paragraph("Reporte de Votación: " + encuesta.getTitulo(), fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        PdfPTable table = new PdfPTable(3); // 3 columnas
        table.setWidthPercentage(100);
        table.addCell("ID Voto");
        table.addCell("Candidato / Opción");
        table.addCell("IP del Votante");

        for (Voto voto : votos) {
            table.addCell(String.valueOf(voto.getId()));
            table.addCell(voto.getOpcion().getTexto());
            table.addCell(voto.getIpUsuario());
        }

        document.add(table);
        document.close();
    }
}