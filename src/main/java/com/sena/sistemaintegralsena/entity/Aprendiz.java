package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern; // Importante
import lombok.Data;

@Data
@Entity
@Table(name = "aprendices")
public class Aprendiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellidos;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    // 1. VALIDACIÓN: Solo dígitos, sin puntos ni espacios
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "El documento solo puede contener números y letras (sin guiones, puntos ni espacios).")
    @Column(unique = true, nullable = false)
    private String numeroDocumento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    // 2. VALIDACIÓN: Unicidad en BD
    @Column(unique = true) 
    private String correo;

    @NotBlank(message = "El celular es obligatorio")
    private String celular;

    @NotBlank(message = "La etapa de formación es obligatoria")
    private String etapaFormacion;

    @NotNull(message = "Debe seleccionar una ficha")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ficha_id", nullable = false)
    private Ficha ficha;

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }
}