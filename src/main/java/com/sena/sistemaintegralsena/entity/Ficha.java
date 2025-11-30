package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // Importante para objetos
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "fichas")
public class Ficha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. SOLO NÚMEROS, MÁXIMO 12
    @NotBlank(message = "El código es obligatorio")
    @Size(max = 12, message = "Máximo 12 dígitos")
    @Pattern(regexp = "^[0-9]+$", message = "El código solo debe contener números")
    @Column(unique = true, nullable = false, length = 12)
    private String codigo;

    // 2. PROGRAMA
    @NotBlank(message = "El programa es obligatorio")
    @Size(min = 5, max = 100, message = "El nombre del programa debe tener entre 5 y 100 caracteres")
    private String programa;

    // 3. COORDINACIÓN (AHORA ES UNA RELACIÓN)
    @NotNull(message = "Debe seleccionar una coordinación")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coordinacion_id", nullable = false)
    private Coordinacion coordinacion;

    // 4. JORNADA
    @NotBlank(message = "Seleccione una jornada")
    private String jornada;

    // 5. MODALIDAD
    @NotBlank(message = "Seleccione una modalidad")
    private String modalidad;
}