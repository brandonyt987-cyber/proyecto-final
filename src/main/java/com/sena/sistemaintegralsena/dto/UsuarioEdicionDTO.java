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

    // 🔑 AJUSTE: Regex que permite VACÍO (^$) O una contraseña segura
    @Pattern(regexp = "^$|^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "Si cambia la contraseña, debe tener mín 8 caracteres, 1 Mayús, 1 Minús, 1 Núm y 1 Especial.")
    private String password;

    @NotNull(message = "Debe seleccionar un rol.")
    private Long rolId;
}