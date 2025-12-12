package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Comite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface ComiteRepository extends JpaRepository<Comite, Long> {

    List<Comite> findByAprendizId(Long aprendizId);

    @Query("SELECT c.coordinacion.nombre, COUNT(c) FROM Comite c " +
           "WHERE c.fechaCreacion BETWEEN :inicio AND :fin " +
           "AND c.activo = true " +
           "AND (:fichaId IS NULL OR c.aprendiz.ficha.id = :fichaId) " +
           "GROUP BY c.coordinacion.nombre")
    List<Object[]> contarPorCoordinacion(@Param("inicio") LocalDate inicio, 
                                         @Param("fin") LocalDate fin,
                                         @Param("fichaId") Long fichaId);
}