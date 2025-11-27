package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern; // 👈 Nuevo import
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "coordinaciones")
public class Coordinacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- DATOS PRINCIPALES ---
    @NotBlank(message = "El nombre de la coordinación es obligatorio")
    @Size(min = 5, message = "La Coordinación debe tener al menos 5 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo debe contener letras y espacios") // 👈 Candado de solo letras
    @Column(unique = true, nullable = false)
    private String nombre; 
}