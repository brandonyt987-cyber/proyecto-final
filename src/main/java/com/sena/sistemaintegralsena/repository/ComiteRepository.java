package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Comite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComiteRepository extends JpaRepository<Comite, Long> {

    // Buscar comités asignados a un aprendiz específico
    List<Comite> findByAprendizId(Long aprendizId);

}