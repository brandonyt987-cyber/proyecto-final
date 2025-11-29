package com.sena.sistemaintegralsena.service.impl;

import com.sena.sistemaintegralsena.entity.Ficha;
import com.sena.sistemaintegralsena.repository.FichaRepository;
import com.sena.sistemaintegralsena.service.FichaService;
import org.springframework.data.domain.Sort; // <--- 1. IMPORTACIÓN NUEVA
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FichaServiceImpl implements FichaService {

    private final FichaRepository fichaRepository;

    public FichaServiceImpl(FichaRepository fichaRepository) {
        this.fichaRepository = fichaRepository;
    }

    @Override
    public List<Ficha> listarTodas() {
        // 2. AJUSTE: Ordenar por ID de forma Ascendente
        return fichaRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public void guardar(Ficha ficha) {
        if (ficha.getId() == null && fichaRepository.existsByCodigo(ficha.getCodigo())) {
            throw new RuntimeException("Ya existe una ficha con el código: " + ficha.getCodigo());
        }
        if (ficha.getId() != null) {
            Ficha existente = fichaRepository.findById(ficha.getId()).orElse(null);
            if (existente != null && !existente.getCodigo().equals(ficha.getCodigo()) 
                && fichaRepository.existsByCodigo(ficha.getCodigo())) {
                throw new RuntimeException("El código " + ficha.getCodigo() + " ya está en uso.");
            }
        }
        fichaRepository.save(ficha);
    }

    @Override
    @Transactional(readOnly = true)
    public Ficha buscarPorId(Long id) {
        return fichaRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        fichaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long totalFichas() {
        return fichaRepository.count();
    }
}