package com.sena.sistemaintegralsena.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

// ¡CRÍTICO! Esta clase debe estar en un archivo separado de PasswordMatchesValidator.
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PasswordMatches {
    String message() default "Las contraseñas no coinciden.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}