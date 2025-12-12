package com.sena.sistemaintegralsena.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.sena.sistemaintegralsena.dto.ReporteDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@Service
public class ReportePdfService {

    // --- PALETA DE COLORES ESTILO "GRADIENT BUTTON" ---
    private static final Color COLOR_GRADIENTE_INICIO = new Color(106, 17, 203); // #6a11cb (Violeta)
    private static final Color COLOR_GRADIENTE_FIN = new Color(37, 117, 252);    // #2575fc (Azul)
    
    private static final Color COLOR_TEXTO = new Color(60, 60, 60);       
    private static final Color COLOR_FONDO_ENCABEZADO = new Color(248, 249, 250); 

    public void exportar(HttpServletResponse response, ReporteDTO datos, LocalDate inicio, LocalDate fin) throws IOException {
        Document document = new Document(PageSize.A4);
        // Capturamos el writer porque lo necesitamos para el degradado
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // 0. LOGO
        try {
            Image logo = Image.getInstance(getClass().getResource("/static/img/logo-sena.png"));
            logo.scaleToFit(50, 50);
            logo.setAlignment(Element.ALIGN_RIGHT);
            document.add(logo);
        } catch (Exception e) {
            System.err.println("No se cargó el logo: " + e.getMessage());
        }

        // 1. TÍTULO (Usamos el color violeta del inicio para el texto del título)
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, COLOR_GRADIENTE_INICIO);
        Paragraph titulo = new Paragraph("Reporte de Gestión", fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(titulo);

        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Paragraph subtitulo = new Paragraph("Sistema Integral NEXUS - Bienestar al Aprendiz", fontSubtitulo);
        subtitulo.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(subtitulo);
        
        document.add(new Paragraph(" ")); 

        // 2. CONTEXTO
        PdfPTable tablaInfo = new PdfPTable(1);
        tablaInfo.setWidthPercentage(100);
        PdfPCell celdaInfo = new PdfPCell();
        celdaInfo.setBackgroundColor(COLOR_FONDO_ENCABEZADO);
        celdaInfo.setBorderColor(new Color(230, 230, 230));
        celdaInfo.setPadding(10);
        
        String textoFecha = " Periodo Analizado: " + (inicio != null ? inicio : "Inicio") + " al " + (fin != null ? fin : "Hoy");
        celdaInfo.setPhrase(new Phrase(textoFecha, FontFactory.getFont(FontFactory.HELVETICA, 10, COLOR_TEXTO)));
        
        tablaInfo.addCell(celdaInfo);
        document.add(tablaInfo);
        
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" ")); 

        // --- SECCIÓN 1 ---
        agregarSubtitulo(document, "1. Atenciones por Tipo de Riesgo");
        
        PdfPTable tablaRiesgos = new PdfPTable(2);
        tablaRiesgos.setWidthPercentage(100);
        tablaRiesgos.setWidths(new float[] { 3f, 1f });
        tablaRiesgos.setSpacingAfter(20); 
        
        // Pasamos el 'writer' para poder dibujar el gradiente
        agregarCabecera(writer, tablaRiesgos, "CATEGORÍA DE RIESGO");
        agregarCabecera(writer, tablaRiesgos, "CASOS");

        for (Map.Entry<String, Long> entry : datos.getAtencionesPorCategoria().entrySet()) {
            agregarCeldaData(tablaRiesgos, entry.getKey(), Element.ALIGN_LEFT);
            agregarCeldaData(tablaRiesgos, entry.getValue().toString(), Element.ALIGN_CENTER);
        }
        document.add(tablaRiesgos);

        // --- SECCIÓN 2 ---
        agregarSubtitulo(document, "2. Gestión de Comités por Coordinación");

        PdfPTable tablaComites = new PdfPTable(2);
        tablaComites.setWidthPercentage(100);
        tablaComites.setWidths(new float[] { 3f, 1f });
        tablaComites.setSpacingAfter(20);

        agregarCabecera(writer, tablaComites, "COORDINACIÓN ACADÉMICA");
        agregarCabecera(writer, tablaComites, "TOTAL");

        for (Map.Entry<String, Long> entry : datos.getComitesPorCoordinacion().entrySet()) {
            agregarCeldaData(tablaComites, entry.getKey(), Element.ALIGN_LEFT);
            agregarCeldaData(tablaComites, entry.getValue().toString(), Element.ALIGN_CENTER);
        }
        document.add(tablaComites);

        // --- SECCIÓN 3 ---
        agregarSubtitulo(document, "3. Desempeño del Equipo Psicosocial");

        PdfPTable tablaRendimiento = new PdfPTable(5); 
        tablaRendimiento.setWidthPercentage(100);
        tablaRendimiento.setWidths(new float[] { 2.5f, 0.8f, 0.8f, 1.2f, 0.8f });

        agregarCabecera(writer, tablaRendimiento, "PROFESIONAL");
        agregarCabecera(writer, tablaRendimiento, "TOTAL");
        agregarCabecera(writer, tablaRendimiento, "ABIERTOS");
        agregarCabecera(writer, tablaRendimiento, "SEGUIMIENTO");
        agregarCabecera(writer, tablaRendimiento, "CERRADOS");

        for (ReporteDTO.RendimientoProfesionalDTO p : datos.getRendimientoEquipo()) {
            agregarCeldaData(tablaRendimiento, p.getNombreProfesional(), Element.ALIGN_LEFT);
            agregarCeldaData(tablaRendimiento, p.getTotalCasos().toString(), Element.ALIGN_CENTER);
            agregarCeldaData(tablaRendimiento, p.getCasosAbiertos().toString(), Element.ALIGN_CENTER);
            agregarCeldaData(tablaRendimiento, p.getCasosSeguimiento().toString(), Element.ALIGN_CENTER);
            agregarCeldaData(tablaRendimiento, p.getCasosCerrados().toString(), Element.ALIGN_CENTER);
        }
        document.add(tablaRendimiento);
        
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph("Generado por NEXUS - " + LocalDate.now(), 
                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, Color.GRAY));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
    }

    private void agregarSubtitulo(Document doc, String texto) throws DocumentException {
        Paragraph p = new Paragraph(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, COLOR_TEXTO));
        p.setSpacingAfter(5);
        doc.add(p);
        
        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);
        PdfPCell celdaLinea = new PdfPCell();
        celdaLinea.setBorder(Rectangle.BOTTOM);
        celdaLinea.setBorderColor(COLOR_GRADIENTE_INICIO); // Usamos el violeta para la línea
        celdaLinea.setBorderWidth(1.5f);
        celdaLinea.setFixedHeight(2);
        linea.addCell(celdaLinea);
        doc.add(linea);
        doc.add(new Paragraph(" "));
    }

    // --- MODIFICADO: AHORA RECIBE EL WRITER PARA DIBUJAR EL GRADIENTE ---
    private void agregarCabecera(PdfWriter writer, PdfPTable tabla, String texto) {
        Font fontCabecera = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        PdfPCell celda = new PdfPCell(new Phrase(texto, fontCabecera));
        
        // IMPORTANTE: Quitamos el color plano
        // celda.setBackgroundColor(COLOR_PRIMARIO); 
        
        // Agregamos el evento que pinta el gradiente
        celda.setCellEvent(new GradientHeaderEvent(writer));
        
        celda.setPaddingTop(8);
        celda.setPaddingBottom(8);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setBorderColor(Color.WHITE); // Bordes blancos para que se vea limpio
        tabla.addCell(celda);
    }

    private void agregarCeldaData(PdfPTable tabla, String texto, int alineacion) {
        Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 10, COLOR_TEXTO);
        PdfPCell celda = new PdfPCell(new Phrase(texto, fontData));
        celda.setPadding(6);
        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setBorderColor(new Color(230, 230, 230)); 
        tabla.addCell(celda);
    }

    // --- CLASE INTERNA PARA PINTAR EL DEGRADADO ---
    // Esto es necesario porque iText no tiene "linear-gradient" nativo en celdas
    public static class GradientHeaderEvent implements PdfPCellEvent {
        private final PdfWriter writer;

        public GradientHeaderEvent(PdfWriter writer) {
            this.writer = writer;
        }

        @Override
        public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
            // Obtenemos el canvas del fondo
            PdfContentByte canvas = canvases[PdfPTable.BACKGROUNDCANVAS];
            
            // Creamos el sombreado (Gradiente Axial) de Izquierda a Derecha
            // x1, y1, x2, y2, colorInicio, colorFin
            PdfShading shading = PdfShading.simpleAxial(writer, 
                    position.getLeft(), position.getBottom(), 
                    position.getRight(), position.getBottom(), 
                    COLOR_GRADIENTE_INICIO, COLOR_GRADIENTE_FIN);
            
            PdfShadingPattern pattern = new PdfShadingPattern(shading);
            
            // Aplicamos el patrón y dibujamos el rectángulo del tamaño de la celda
            canvas.saveState();
            canvas.setShadingFill(pattern);
            canvas.rectangle(position.getLeft(), position.getBottom(), position.getWidth(), position.getHeight());
            canvas.fill();
            canvas.restoreState();
        }
    }
}