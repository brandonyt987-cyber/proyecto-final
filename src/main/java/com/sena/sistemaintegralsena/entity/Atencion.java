package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "atenciones")
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- DATOS GENERALES DEL CASO ---
    @NotBlank(message = "El estado del caso es obligatorio")
    private String estadoCaso; // Abierto, Cerrado, En Seguimiento

    @NotBlank(message = "La categoría es obligatoria")
    private String categoriaDesercion; 

    @NotBlank(message = "El campo 'Remitido Por' es obligatorio")
    private String remitidoPor; 

    private String atencionFamiliar;

    private LocalDateTime fechaCreacionRegistro;

    // --- SEGUIMIENTOS ---
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaConsulta1;
    @Column(columnDefinition = "TEXT")
    private String observaciones1;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaConsulta2;
    @Column(columnDefinition = "TEXT")
    private String observaciones2;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaConsulta3;
    @Column(columnDefinition = "TEXT")
    private String observaciones3;

    // --- RELACIONES ---
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aprendiz_id", nullable = false)
    private Aprendiz aprendiz;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario profesional; 

    @PrePersist
    public void prePersist() {
        this.fechaCreacionRegistro = LocalDateTime.now();
    }
}