package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past; // Validación para fechas pasadas
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat; // Formato fecha

import java.time.LocalDate;
import java.time.Period; // Para calcular edad

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
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "El documento solo puede contener números y letras.")
    @Column(unique = true, nullable = false)
    private String numeroDocumento;
    
    // --- NUEVO CAMPO: FECHA NACIMIENTO ---
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo inválido")
    @Column(unique = true) 
    private String correo;

    @NotBlank(message = "El celular es obligatorio")
    private String celular;

    @NotBlank(message = "La etapa de formación es obligatoria")
    private String etapaFormacion;

    @NotNull(message = "Debe seleccionar una ficha")
    // 🔑 AJUSTE CRÍTICO: Se cambia LAZY a EAGER para garantizar la carga de la Ficha.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ficha_id", nullable = false)
    private Ficha ficha;

    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }

    // --- MÉTODO CALCULADO: EDAD ---
    public int getEdad() {
        if (this.fechaNacimiento == null) return 0;
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }
}