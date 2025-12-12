package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AprendizRepository extends JpaRepository<Aprendiz, Long> {
    
    boolean existsByNumeroDocumento(String numeroDocumento);
    boolean existsByCorreo(String correo);

    Aprendiz findByNumeroDocumento(String numeroDocumento);

    // Listar todos por ficha
    List<Aprendiz> findByFichaId(Long fichaId);
    
    // Listar solo activos por ficha (Para los desplegables de Comités)
    List<Aprendiz> findByFichaIdAndActivoTrue(Long fichaId);
    boolean existsByFichaIdAndEsVoceroTrueAndActivoTrue(Long fichaId);
}