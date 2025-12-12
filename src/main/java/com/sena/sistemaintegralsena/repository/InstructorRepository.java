package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstructorRepository extends JpaRepository<Instructor, Long> {
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByCorreo(String correo);
    
    List<Instructor> findByActivoTrue();
}