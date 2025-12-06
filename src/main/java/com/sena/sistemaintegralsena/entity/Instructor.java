package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern; // 👈 NUEVO IMPORT
import lombok.Data;

@Data
@Entity
@Table(name = "instructores")
public class Instructor {

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
    @Column(unique = true, nullable = false)
    private String numeroDocumento;

    @NotBlank(message = "La profesión es obligatoria")
    private String profesion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El teléfono solo debe contener números (sin espacios, guiones o símbolos).") // 👈 VALIDACIÓN NUMÉRICA
    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Column(unique = true)
    private String correo;
    
    // Foreign Key a Coordinacion
    @NotNull(message = "Debe asignar una coordinación")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinacion_id", nullable = false)
    private Coordinacion coordinacion;

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }
}