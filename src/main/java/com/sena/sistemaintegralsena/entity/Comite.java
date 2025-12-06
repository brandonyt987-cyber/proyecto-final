package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "comites")
public class Comite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- RELACIONES ---
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprendiz_id", nullable = false)
    private Aprendiz aprendiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario profesional; 

    @NotNull(message = "Seleccione el instructor a cargo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinacion_id", nullable = false)
    private Coordinacion coordinacion;

    // --- DATOS DEL EVENTO ---

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    // 🔑 AJUSTE: Mensaje simple. El rango lo valida JavaScript.
    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @NotBlank(message = "El enlace o lugar es obligatorio")
    private String enlace;

    @NotBlank(message = "Seleccione el tipo de falta")
    private String tipoFalta; 

    @NotBlank(message = "El motivo es obligatorio")
    @Column(columnDefinition = "TEXT")
    private String motivo;

    // --- ASISTENTES ---

    @NotBlank(message = "Seleccione el profesional de Bienestar")
    private String profesionalBienestar; 

    @NotBlank(message = "El representante de aprendices es obligatorio")
    private String representanteAprendices;

    // --- RESULTADOS ---
    
    @NotBlank(message = "La recomendación es obligatoria")
    @Column(columnDefinition = "TEXT")
    private String recomendacion; 

    @NotBlank(message = "El plan de mejoramiento es obligatorio")
    @Column(columnDefinition = "TEXT")
    private String planMejoramiento; 

    @NotNull(message = "La fecha plazo es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPlazo;

    @NotBlank(message = "Las observaciones son obligatorias")
    @Column(columnDefinition = "TEXT")
    private String observaciones; 

    @NotNull(message = "Indique si hay Paz y Salvo")
    private Boolean pazSalvo; 
}