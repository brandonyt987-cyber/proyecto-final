package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "coordinaciones")
public class Coordinacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la coordinación es obligatorio")
    @Column(unique = true, nullable = false)
    private String nombre; // Ej: "Teleinformática", "Logística", "Salud"
}