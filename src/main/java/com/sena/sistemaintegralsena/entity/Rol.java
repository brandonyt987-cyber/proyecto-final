package com.sena.sistemaintegralsena.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usado por getNombre()
    private String nombre; 

    // Constructor vacío (siempre recomendado para JPA) ojito
    public Rol() {}
}