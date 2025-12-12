package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.dto.ReporteDTO;
import com.sena.sistemaintegralsena.repository.AtencionRepository;
import com.sena.sistemaintegralsena.repository.ComiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteService {

    private final AtencionRepository atencionRepository;
    private final ComiteRepository comiteRepository;

    public ReporteDTO obtenerDatosDashboard(LocalDate inicio, LocalDate fin, Long fichaId, Long profId) {
        System.out.println("---- [DEBUG] INICIANDO REPORTE ----");
        
        ReporteDTO dto = new ReporteDTO();

        // Configurar fechas
        LocalDateTime fechaInicio = (inicio != null) ? inicio.atStartOfDay() : LocalDate.now().withDayOfYear(1).atStartOfDay();
        LocalDateTime fechaFin = (fin != null) ? fin.atTime(23, 59, 59) : LocalDateTime.now();
        
        LocalDate fechaInicioDate = (inicio != null) ? inicio : LocalDate.now().withDayOfYear(1);
        LocalDate fechaFinDate = (fin != null) ? fin : LocalDate.now();

        // 1. GRÁFICA DE TORTA (Atenciones)
        List<Object[]> dataAtenciones = atencionRepository.contarPorCategoria(fechaInicio, fechaFin, fichaId, profId);
        System.out.println(">> [DEBUG] Atenciones encontradas: " + dataAtenciones.size());
        
        Map<String, Long> mapaAtenciones = new HashMap<>();
        for (Object[] fila : dataAtenciones) {
            String categoria = (String) fila[0];
            Long cantidad = (Long) fila[1];
            mapaAtenciones.put(categoria != null ? categoria : "Sin Categoría", cantidad);
        }
        dto.setAtencionesPorCategoria(mapaAtenciones);

        // 2. GRÁFICA DE BARRAS (Comités)
        List<Object[]> dataComites = comiteRepository.contarPorCoordinacion(fechaInicioDate, fechaFinDate, fichaId);
        System.out.println(">> [DEBUG] Comités encontrados: " + dataComites.size());

        Map<String, Long> mapaComites = new HashMap<>();
        for (Object[] fila : dataComites) {
            String coord = (String) fila[0];
            Long cantidad = (Long) fila[1];
            mapaComites.put(coord != null ? coord : "Sin Coord.", cantidad);
        }
        dto.setComitesPorCoordinacion(mapaComites);

        // 3. TABLA DE RENDIMIENTO
        List<Object[]> dataRendimiento = atencionRepository.contarRendimientoEquipo(fechaInicio, fechaFin, profId);
        System.out.println(">> [DEBUG] Datos de rendimiento encontrados: " + dataRendimiento.size());
        
        Map<String, ReporteDTO.RendimientoProfesionalDTO> mapaRendimiento = new HashMap<>();

        for (Object[] fila : dataRendimiento) {
            String nombreProf = (String) fila[0];
            String estado = (String) fila[1];
            Long cantidad = (Long) fila[2];

            if (nombreProf == null) continue;

            mapaRendimiento.putIfAbsent(nombreProf, new ReporteDTO.RendimientoProfesionalDTO(nombreProf, 0L, 0L, 0L, 0L));
            ReporteDTO.RendimientoProfesionalDTO profDto = mapaRendimiento.get(nombreProf);
            
            profDto.setTotalCasos(profDto.getTotalCasos() + cantidad);

            // Lógica flexible para detectar mayusculas/minsculas
            if (estado != null) {
                String estadoNorm = estado.trim().toLowerCase();
                System.out.println("   Procesando estado: " + estadoNorm + " -> Cantidad: " + cantidad);
                
                if (estadoNorm.contains("abierto")) {
                    profDto.setCasosAbiertos(profDto.getCasosAbiertos() + cantidad);
                } else if (estadoNorm.contains("cerrado")) {
                    profDto.setCasosCerrados(profDto.getCasosCerrados() + cantidad);
                } else if (estadoNorm.contains("seguimiento")) {
                    profDto.setCasosSeguimiento(profDto.getCasosSeguimiento() + cantidad);
                }
            }
        }
        dto.setRendimientoEquipo(new ArrayList<>(mapaRendimiento.values()));

        return dto;
    }
}