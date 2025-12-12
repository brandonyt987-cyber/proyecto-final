package com.sena.sistemaintegralsena.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioEdicionDTO {

    @NotNull
    private Long id; 

    @NotEmpty(message = "El nombre es obligatorio.")
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras y espacios.")
    private String nombre;

    @NotEmpty(message = "El email es obligatorio.")
    @Email(message = "Debe ser una dirección de correo válida.")
    private String email;

    // Contraseña opcional en edición
    @Pattern(regexp = "^$|^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "Si cambia la contraseña, debe cumplir con los requisitos de seguridad.")
    private String password;

    @NotNull(message = "Debe seleccionar un rol.")
    private Long rolId;
}