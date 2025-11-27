package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Importa todas las validaciones
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.time.Period;

@Data
@Entity
@Table(name = "aprendices")
public class Aprendiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- IDENTIFICACIÓN ---
    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 5, message = "El documento debe tener mínimo 5 caracteres") // 👈 Mínimo 5
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "El documento solo puede contener números y letras")
    @Column(unique = true, nullable = false)
    private String numeroDocumento;

    // --- DATOS PERSONALES ---
    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo debe contener letras y espacios") // 👈 Solo letras
    private String nombres;

    @NotBlank(message = "El apellido es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo debe contener letras y espacios") // 👈 Solo letras
    private String apellidos;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    // --- FORMACIÓN ---
    @NotBlank(message = "La etapa de formación es obligatoria")
    private String etapaFormacion;

    // --- CONTACTO ---
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato inválido")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", 
        message = "El correo no debe contener tildes, 'ñ' ni caracteres especiales."
    )
    @Column(unique = true) 
    private String correo;

    @NotBlank(message = "El celular es obligatorio")
    @Pattern(regexp = "^[0-9]{10}$", message = "El celular debe tener exactamente 10 números") // 👈 Exactamente 10
    private String celular;

    // --- RELACIONES ---
    @NotNull(message = "Debe seleccionar una ficha")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ficha_id", nullable = false)
    private Ficha ficha;

    public String getNombreCompleto() { return this.nombres + " " + this.apellidos; }
    
    public int getEdad() {
        if (this.fechaNacimiento == null) return 0;
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }
}