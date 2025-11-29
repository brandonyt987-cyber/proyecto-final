package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Coordinacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordinacionRepository extends JpaRepository<Coordinacion, Long> {
    // Para validar que no creen dos con el mismo nombre
    boolean existsByNombre(String nombre);
}