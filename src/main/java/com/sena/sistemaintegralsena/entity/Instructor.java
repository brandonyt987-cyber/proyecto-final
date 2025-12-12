package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; 
import lombok.Data;

@Data
@Entity
@Table(name = "instructores")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- IDENTIFICACIÓN ---
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 5, max = 12, message = "El documento debe tener al menos 5 caracteres y max 12") 
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "El documento solo puede contener números y letras")
    @Column(unique = true, nullable = false)
    private String numeroDocumento;

    // --- INFORMACIÓN PERSONAL ---
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 5, max = 50, message = "El nombre debe tener entre 5 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo debe contener letras y espacios") 
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 5, max = 50, message = "El apellido debe tener entre 5 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo debe contener letras y espacios") 
    private String apellidos;

    @NotBlank(message = "La profesión es obligatoria")
    @Size(min = 5, max = 70, message = "La profesión debe tener al menos 5 caracteres, max 70")
    private String profesion;

    // --- CONTACTO ---
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Column(unique = true)
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El celular debe tener exactamente 10 números") 
    private String telefono;

    // --- RELACIONES ---
    @NotNull(message = "Debe asignar una coordinación")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinacion_id", nullable = false)
    private Coordinacion coordinacion;

    
    @Column(columnDefinition = "boolean default true")
    private boolean activo = true;

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }
}