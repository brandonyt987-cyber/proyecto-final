package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Atencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {
    
    List<Atencion> findByAprendizId(Long aprendizId);
    
    // Buscar por ID de Usuario (Profesional)
    List<Atencion> findByProfesionalId(Long usuarioId);
    
    // --- CONSULTAS INTELIGENTES PARA REPORTES ---

    // 1. Gráfica de Torta
    @Query("SELECT a.categoriaDesercion, COUNT(a) FROM Atencion a " +
           "WHERE a.fechaCreacionRegistro BETWEEN :inicio AND :fin " +
           "AND a.activo = true " + 
           "AND (:fichaId IS NULL OR a.aprendiz.ficha.id = :fichaId) " +
           "AND (:profId IS NULL OR a.profesional.id = :profId) " +
           "GROUP BY a.categoriaDesercion")
    List<Object[]> contarPorCategoria(@Param("inicio") LocalDateTime inicio, 
                                      @Param("fin") LocalDateTime fin,
                                      @Param("fichaId") Long fichaId,
                                      @Param("profId") Long profId);

    // 2. Tabla de Rendimiento: Agrupa por Profesional y Estado
    @Query("SELECT a.profesional.nombre, a.estadoCaso, COUNT(a) FROM Atencion a " +
           "WHERE a.fechaCreacionRegistro BETWEEN :inicio AND :fin " +
           "AND a.activo = true " +
           "AND (:profId IS NULL OR a.profesional.id = :profId) " +
           "GROUP BY a.profesional.nombre, a.estadoCaso")
    List<Object[]> contarRendimientoEquipo(@Param("inicio") LocalDateTime inicio, 
                                           @Param("fin") LocalDateTime fin,
                                           @Param("profId") Long profId);
}