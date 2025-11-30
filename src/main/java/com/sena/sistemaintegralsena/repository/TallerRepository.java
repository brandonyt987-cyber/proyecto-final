package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Taller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TallerRepository extends JpaRepository<Taller, Long> {
    
    // Ver talleres asignados a un profesional
    List<Taller> findByProfesionalId(Long usuarioId);

    // Ver talleres de una ficha específica
    List<Taller> findByFichaId(Long fichaId);
}