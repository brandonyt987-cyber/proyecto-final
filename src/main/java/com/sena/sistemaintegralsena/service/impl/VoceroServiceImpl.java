package com.sena.sistemaintegralsena.service.impl;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import com.sena.sistemaintegralsena.entity.Vocero;
import com.sena.sistemaintegralsena.repository.AprendizRepository;
import com.sena.sistemaintegralsena.repository.VoceroRepository;
import com.sena.sistemaintegralsena.service.VoceroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VoceroServiceImpl implements VoceroService {

    private final VoceroRepository repository;
    private final AprendizRepository aprendizRepository;

    public VoceroServiceImpl(VoceroRepository repository, AprendizRepository aprendizRepository) {
        this.repository = repository;
        this.aprendizRepository = aprendizRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Vocero> listarTodos() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void guardar(Vocero vocero) {
        // 1. Cargar Aprendiz
        Aprendiz aprendizCompleto = aprendizRepository.findById(vocero.getAprendiz().getId())
                .orElseThrow(() -> new RuntimeException("Aprendiz no encontrado."));
        vocero.setAprendiz(aprendizCompleto); 

        // --- LÓGICA DE RAZÓN DE CAMBIO ---
        if (vocero.getId() == null) {
            // CASO CREAR: Asignamos un valor por defecto automático
            vocero.setRazonCambio("Asignación Inicial del Vocero");
        } else {
            // CASO EDITAR: Validamos que el usuario haya escrito algo
            if (vocero.getRazonCambio() == null || vocero.getRazonCambio().trim().isEmpty()) {
                throw new RuntimeException("Es obligatorio escribir la razón del cambio o actualización.");
            }
        }

        // 2. Validación de Ficha (Misma Ficha) al Editar
        if (vocero.getId() != null) {
            Vocero voceroOriginal = repository.findById(vocero.getId()).orElse(null);
            if (voceroOriginal != null) {
                Long fichaOriginal = voceroOriginal.getAprendiz().getFicha().getId();
                Long fichaNueva = aprendizCompleto.getFicha().getId();
                if (!fichaOriginal.equals(fichaNueva)) {
                    throw new RuntimeException("El nuevo vocero debe ser de la misma ficha (" + voceroOriginal.getAprendiz().getFicha().getCodigo() + ").");
                }
            }
        }

        // 3. Validación de Unicidad
        Long fichaId = aprendizCompleto.getFicha().getId();
        Vocero existingVocero = repository.findByAprendizFichaId(fichaId);

        if (existingVocero != null && !existingVocero.getId().equals(vocero.getId())) {
             throw new RuntimeException("La ficha " + aprendizCompleto.getFicha().getCodigo() + " ya tiene un vocero activo.");
        }
        
        repository.save(vocero);
    }

    @Override
    @Transactional(readOnly = true)
    public Vocero buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long totalVoceros() {
        return repository.count();
    }
}