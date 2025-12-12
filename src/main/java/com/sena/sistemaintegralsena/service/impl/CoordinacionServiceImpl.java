package com.sena.sistemaintegralsena.service.impl;

import com.sena.sistemaintegralsena.entity.Coordinacion;
import com.sena.sistemaintegralsena.repository.CoordinacionRepository;
import com.sena.sistemaintegralsena.service.CoordinacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class CoordinacionServiceImpl implements CoordinacionService {

    private final CoordinacionRepository repository;

    public CoordinacionServiceImpl(CoordinacionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Coordinacion> listarTodas() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    @Transactional
    public void guardar(Coordinacion coordinacion) {
        
        if (coordinacion.getId() == null && repository.existsByNombre(coordinacion.getNombre())) {
             throw new RuntimeException("La coordinación '" + coordinacion.getNombre() + "' ya existe.");
        }
        repository.save(coordinacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Coordinacion buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    // --- NUEVA LÓGICA DE ESTADO ---
    @Override
    @Transactional
    public void cambiarEstado(Long id) {
        Coordinacion coord = repository.findById(id).orElse(null);
        if (coord != null) {
            coord.setActivo(!coord.isActivo()); 
            repository.save(coord);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long totalCoordinaciones() {
        return repository.count();
    }
}