package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
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

    // --- RELACIONES AUTOMÁTICAS ---
    @NotNull(message = "Debe seleccionar un aprendiz")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprendiz_id", nullable = false)
    private Aprendiz aprendiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario profesional; // Usuario que registra en el sistema

    // --- DATOS DEL COMITÉ ---
    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "La fecha no puede ser anterior a hoy")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime hora;

    @NotBlank(message = "El enlace o lugar es obligatorio")
    private String enlace;

    @NotBlank(message = "Seleccione el tipo de falta")
    private String tipoFalta; 

    @NotBlank(message = "El motivo es obligatorio")
    @Column(columnDefinition = "TEXT")
    private String motivo;

    // --- ASISTENTES Y RESPONSABLES (FALTABAN ESTOS) ---

    @NotBlank(message = "Seleccione el profesional que asiste")
    private String profesionalBienestar; // Nombre tomado del select

    @NotBlank(message = "Indique el representante de aprendices")
    private String representanteAprendices; 

    @NotBlank(message = "Indique el profesional a cargo del plan")
    private String profesionalCargoPlan; 

    // --- RESULTADOS Y COMPROMISOS ---
    
    @NotBlank(message = "La recomendación es obligatoria")
    @Column(columnDefinition = "TEXT")
    private String recomendacion; 

    @NotBlank(message = "El plan es obligatorio")
    @Column(columnDefinition = "TEXT")
    private String planMejoramiento;

    @NotNull(message = "La fecha plazo es obligatoria")
    @FutureOrPresent(message = "La fecha plazo debe ser futura")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPlazo;

    @NotBlank(message = "Las observaciones son obligatorias")
    @Column(columnDefinition = "TEXT")
    private String observaciones;

    private boolean pazSalvo; 
}