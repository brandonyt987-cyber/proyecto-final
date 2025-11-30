package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Atencion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {
    // Ver historial de un aprendiz
    List<Atencion> findByAprendizId(Long aprendizId);
    
    // Ver mis casos (para el profesional logueado)
    List<Atencion> findByProfesionalId(Long usuarioId);
}