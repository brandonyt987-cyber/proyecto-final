package com.sena.sistemaintegralsena.entity;

import lombok.Data; // Importar Lombok
import jakarta.persistence.*;

@Data // <-- CRÍTICO: Genera todos los get/set para Usuario
@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; 
    private String email; 
    private String password;
    private String rol; // Usado por getRol() y setRol()
    private boolean enabled; // Usado por isEnabled()

    // Constructor vacío (siempre recomendado para JPA)
    public Usuario() {} 
}