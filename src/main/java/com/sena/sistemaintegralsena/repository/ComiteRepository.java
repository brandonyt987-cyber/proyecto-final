package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Comite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComiteRepository extends JpaRepository<Comite, Long> {

    // Buscar comités asignados a un aprendiz específico (Historial del aprendiz)
    List<Comite> findByAprendizId(Long aprendizId);

    // Buscar comités creados por un profesional específico (Mis Comités)
    List<Comite> findByProfesionalId(Long usuarioId);
    
    // Buscar por fecha (agenda)
    // List<Comite> findByFecha(LocalDate fecha); 
}