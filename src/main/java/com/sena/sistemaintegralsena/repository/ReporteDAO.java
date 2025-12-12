package com.sena.sistemaintegralsena.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ReporteDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // 1. Atenciones
    @Transactional
    public List<Object[]> contarAtencionesPorCategoria(LocalDate inicio, LocalDate fin, Long fichaId, Long profesionalId) {
        entityManager.clear(); 
        
        String jpql = "SELECT a.categoriaDesercion, COUNT(a) FROM Atencion a " +
                      "JOIN a.aprendiz ap " + 
                      "WHERE (:inicio IS NULL OR a.fechaCreacionRegistro >= :inicio) " +
                      "AND (:fin IS NULL OR a.fechaCreacionRegistro <= :fin) " +
                      "AND (:fichaId IS NULL OR ap.ficha.id = :fichaId) " +
                      "AND (:profId IS NULL OR a.profesional.id = :profId) " +
                      "GROUP BY a.categoriaDesercion";
        
        Query query = entityManager.createQuery(jpql);
        
        LocalDateTime inicioTime = (inicio != null) ? inicio.atStartOfDay() : null;
        LocalDateTime finTime = (fin != null) ? fin.atTime(23, 59, 59) : null;

        query.setParameter("inicio", inicioTime);
        query.setParameter("fin", finTime);
        query.setParameter("fichaId", fichaId);
        query.setParameter("profId", profesionalId);
        
        return query.getResultList();
    }

    // 2. Comites
    @Transactional
    public List<Object[]> contarComitesPorCoordinacion(LocalDate inicio, LocalDate fin, Long fichaId) {
        entityManager.clear(); 
        
        String jpql = "SELECT c.coordinacion.nombre, COUNT(c) FROM Comite c " +
                      "JOIN c.aprendiz ap " +
                      "WHERE (:inicio IS NULL OR c.fecha >= :inicio) " +
                      "AND (:fin IS NULL OR c.fecha <= :fin) " +
                      "AND (:fichaId IS NULL OR ap.ficha.id = :fichaId) " +
                      "GROUP BY c.coordinacion.nombre";
        
        Query query = entityManager.createQuery(jpql);
        query.setParameter("inicio", inicio);
        query.setParameter("fin", fin);
        query.setParameter("fichaId", fichaId);
        
        return query.getResultList();
    }

    // 3. Rendimiento 
    @Transactional
    public List<Object[]> obtenerRendimientoProfesionales(LocalDate inicio, LocalDate fin, Long profesionalId) {
        entityManager.clear(); 
        
        String jpql = "SELECT a.profesional.nombre, " +
                      "COUNT(a), " +
                      "SUM(CASE WHEN a.estadoCaso LIKE 'Abierto%' THEN 1 ELSE 0 END), " +
                      "SUM(CASE WHEN a.estadoCaso LIKE '%Seguimiento%' THEN 1 ELSE 0 END), " +
                      "SUM(CASE WHEN a.estadoCaso LIKE 'Cerrado%' THEN 1 ELSE 0 END) " +
                      "FROM Atencion a " +
                      "WHERE (:inicio IS NULL OR a.fechaCreacionRegistro >= :inicio) " +
                      "AND (:fin IS NULL OR a.fechaCreacionRegistro <= :fin) " +
                      "AND (:profId IS NULL OR a.profesional.id = :profId) " +
                      "GROUP BY a.profesional.nombre";
                      
        Query query = entityManager.createQuery(jpql);
        
        LocalDateTime inicioTime = (inicio != null) ? inicio.atStartOfDay() : null;
        LocalDateTime finTime = (fin != null) ? fin.atTime(23, 59, 59) : null;

        query.setParameter("inicio", inicioTime);
        query.setParameter("fin", finTime);
        query.setParameter("profId", profesionalId);
        
        return query.getResultList();
    }
}