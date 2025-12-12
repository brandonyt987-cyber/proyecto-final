package com.sena.sistemaintegralsena.entity;

import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; 
    
    @Column(unique = true)
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
        message = "El correo no debe contener tildes, 'ñ' ni caracteres especiales."
    )
    private String email; 
    
    private String password;
    
    private String rol; 
    
    
    @Column(columnDefinition = "boolean default true")
    private boolean enabled = true; 

    public Usuario() {} 
}