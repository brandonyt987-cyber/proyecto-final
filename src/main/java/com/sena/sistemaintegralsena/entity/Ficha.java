package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    // 2. LETRAS Y NÚMEROS (Longitud razonable de 100)
    @NotBlank(message = "El programa es obligatorio")
    @Size(min = 5, max = 100, message = "El nombre del programa debe tener entre 5 y 100 caracteres")
    private String programa;

    // 3. COORDINACIÓN (Texto libre, máx 80 caracteres)
    @NotBlank(message = "La coordinación es obligatoria")
    @Size(max = 80, message = "Nombre de coordinación muy largo")
    private String coordinacion;

    // 4. JORNADA (Validamos que no esté vacío, el HTML limita las opciones)
    @NotBlank(message = "Seleccione una jornada")
    private String jornada;

    // 5. MODALIDAD (Validamos que no esté vacío)
    @NotBlank(message = "Seleccione una modalidad")
    private String modalidad;
}