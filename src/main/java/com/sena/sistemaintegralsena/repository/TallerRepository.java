package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Taller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TallerRepository extends JpaRepository<Taller, Long> {
    
    List<Taller> findByProfesionalId(Long usuarioId);
    List<Taller> findByFichaId(Long fichaId);
    List<Taller> findByActivoTrue();
}