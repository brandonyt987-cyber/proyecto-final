package com.sena.sistemaintegralsena.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
public class ReporteDTO {
    // Gráfica de Torta: Categoría vs Cantidad
    private Map<String, Long> atencionesPorCategoria; 

    // Gráfica de Barras: Coordinación vs Cantidad de Comités
    private Map<String, Long> comitesPorCoordinacion;

    // Tabla: Desglose por Profesional
    private List<RendimientoProfesionalDTO> rendimientoEquipo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RendimientoProfesionalDTO {
        private String nombreProfesional;
        private Long totalCasos;
        private Long casosAbiertos;
        private Long casosSeguimiento; 
        private Long casosCerrados;
    }
}