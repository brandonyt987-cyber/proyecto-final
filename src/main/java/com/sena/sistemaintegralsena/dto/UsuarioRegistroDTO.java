package com.sena.sistemaintegralsena.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data 
public class UsuarioRegistroDTO {

    @NotEmpty(message = "El nombre es obligatorio.")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras y espacios.")
    private String nombre;

    @NotEmpty(message = "El email es obligatorio.")
    @Email(message = "Debe ser una dirección de correo válida.")
    private String email;

    @NotEmpty(message = "La contraseña es obligatoria.")
    @Size(min = 8, message = "Mínimo 8 caracteres.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "Debe contener al menos una mayúscula, una minúscula, un número y un carácter especial (@$!%*?&).")
    private String password;

    @NotEmpty(message = "Debe confirmar la contraseña.")
    private String confirmPassword;

    @NotNull(message = "Debe seleccionar un rol.")
    private Long rolId; 
}