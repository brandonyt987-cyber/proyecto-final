package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AprendizRepository extends JpaRepository<Aprendiz, Long> {
    
    // Para validar duplicados antes de guardar
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByCorreo(String correo);

    // Para buscar un aprendiz específico (útil para Comités)
    Aprendiz findByNumeroDocumento(String numeroDocumento);

    // Para listar todos los aprendices de una Ficha específica
    List<Aprendiz> findByFichaId(Long fichaId);
}