package com.sena.sistemaintegralsena.util;

import com.sena.sistemaintegralsena.dto.UsuarioRegistroDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// ¡CRÍTICO! Esta clase debe estar en su propio archivo.
public class PasswordMatchesValidator 
    implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        UsuarioRegistroDTO user = (UsuarioRegistroDTO) obj;
        
        // Comprueba la igualdad
        return user.getPassword() != null && user.getPassword().equals(user.getConfirmPassword());
    }
}