package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "talleres")
public class Taller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- DATOS DEL TALLER ---
    @NotBlank(message = "El nombre del taller es obligatorio")
    private String nombreTaller;

    @NotNull(message = "El cupo es obligatorio")
    @Min(value = 1, message = "El cupo debe ser al menos 1")
    private Integer cupo;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime horaFin;

    // --- RELACIONES ---
    @NotNull(message = "Debe asignar un profesional")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario profesional;

    @NotNull(message = "Debe seleccionar una ficha")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ficha_id", nullable = false)
    private Ficha ficha;

    // --- NUEVO: ESTADO ---
    @Column(columnDefinition = "boolean default true")
    private boolean activo = true;
}