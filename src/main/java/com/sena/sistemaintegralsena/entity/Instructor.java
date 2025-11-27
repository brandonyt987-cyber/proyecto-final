package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Importa todas las validaciones
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
    @Size(min = 5, message = "El documento debe tener al menos 5 caracteres") // 👈 Mínimo 5
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "El documento solo puede contener números y letras (sin guiones ni espacios)")
    @Column(unique = true, nullable = false)
    private String numeroDocumento;

    // --- INFORMACIÓN PERSONAL ---
    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo debe contener letras y espacios") // 👈 Solo letras
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo debe contener letras y espacios") // 👈 Solo letras
    private String apellidos;

    @NotBlank(message = "La profesión es obligatoria")
    @Size(min = 5, message = "La profesión debe tener al menos 5 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "La profesión solo debe contener letras") // 👈 Solo letras
    private String profesion;

    // --- CONTACTO ---
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
        message = "El correo no debe contener tildes, 'ñ' ni caracteres especiales."
    )
    @Column(unique = true)
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El celular debe tener exactamente 10 números") // 👈 Exactamente 10
    private String telefono;

    // --- RELACIONES ---
    @NotNull(message = "Debe asignar una coordinación")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinacion_id", nullable = false)
    private Coordinacion coordinacion;

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }
}