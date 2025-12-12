package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Ficha;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FichaRepository extends JpaRepository<Ficha, Long> {
    boolean existsByCodigo(String codigo);
    Ficha findByCodigo(String codigo);

    List<Ficha> findByActivoTrue();
}