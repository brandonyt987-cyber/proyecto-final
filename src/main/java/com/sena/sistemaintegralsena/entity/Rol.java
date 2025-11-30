package com.sena.sistemaintegralsena.entity;

import lombok.Data; // Importar Lombok
import jakarta.persistence.*;

@Data // <-- CRÍTICO: Genera todos los get/set para Rol
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usado por getNombre()
    private String nombre; 

    // Constructor vacío (siempre recomendado para JPA)
    public Rol() {}
}