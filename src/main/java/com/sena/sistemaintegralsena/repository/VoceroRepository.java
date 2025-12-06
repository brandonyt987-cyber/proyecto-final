package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Vocero;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoceroRepository extends JpaRepository<Vocero, Long> {
    // Para evitar que un Aprendiz sea Vocero más de una vez (con el @OneToOne unique=true también se evita)
    boolean existsByAprendizId(Long aprendizId);
    Vocero findByAprendizFichaId(Long fichaId);
}