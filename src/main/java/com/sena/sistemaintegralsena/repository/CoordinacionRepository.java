package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Coordinacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoordinacionRepository extends JpaRepository<Coordinacion, Long> {
    boolean existsByNombre(String nombre);
    
    List<Coordinacion> findByActivoTrue();
}