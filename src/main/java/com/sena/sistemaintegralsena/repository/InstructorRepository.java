package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    // Para evitar duplicados en número de documento
    boolean existsByNumeroDocumento(String numeroDocumento);
    // Para evitar duplicados en correo
    boolean existsByCorreo(String correo);
}