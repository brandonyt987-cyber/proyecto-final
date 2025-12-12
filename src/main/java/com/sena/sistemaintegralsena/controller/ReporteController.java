package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.dto.ReporteDTO;
import com.sena.sistemaintegralsena.repository.FichaRepository;
import com.sena.sistemaintegralsena.repository.UsuarioRepository;
import com.sena.sistemaintegralsena.service.ReportePdfService;
import com.sena.sistemaintegralsena.service.ReporteService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final ReportePdfService reportePdfService;
    private final FichaRepository fichaRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public String verReportes(
            Model model,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long fichaId,
            @RequestParam(required = false) Long profesionalId
    ) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioAnio = LocalDate.of(hoy.getYear(), 1, 1); 

        if (fechaInicio == null) fechaInicio = inicioAnio;
        if (fechaFin == null) fechaFin = hoy; 

        
        ReporteDTO reportes = reporteService.obtenerDatosDashboard(fechaInicio, fechaFin, fichaId, profesionalId);
        
        model.addAttribute("listaFichas", fichaRepository.findAll());
        model.addAttribute("listaProfesionales", usuarioRepository.findAll()); 
        model.addAttribute("reporteData", reportes);
        
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("fichaSeleccionada", fichaId);
        model.addAttribute("profesionalSeleccionado", profesionalId);
        model.addAttribute("fechaMinima", inicioAnio);

        return "reportes/ver_reportes"; 
    }

    @GetMapping("/exportar-pdf")
    public void exportarPdf(
            HttpServletResponse response,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long fichaId,
            @RequestParam(required = false) Long profesionalId
    ) throws IOException {
        
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        String currentDateTime = dateFormatter.format(new Date());
        
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Reporte_Gestion_NEXUS_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        if (fechaInicio == null) fechaInicio = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        if (fechaFin == null) fechaFin = LocalDate.now();

        ReporteDTO datos = reporteService.obtenerDatosDashboard(fechaInicio, fechaFin, fichaId, profesionalId);
        reportePdfService.exportar(response, datos, fechaInicio, fechaFin);
    }
}