package com.sena.sistemaintegralsena.service.impl;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import com.sena.sistemaintegralsena.repository.AprendizRepository;
import com.sena.sistemaintegralsena.service.AprendizService;
import org.springframework.data.domain.Sort; // <--- IMPORTANTE PARA EL ORDEN
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AprendizServiceImpl implements AprendizService {

    private final AprendizRepository aprendizRepository;

    public AprendizServiceImpl(AprendizRepository aprendizRepository) {
        this.aprendizRepository = aprendizRepository;
    }

    @Override
    public List<Aprendiz> listarTodos() {
        // AJUSTE: Ordenar por ID Ascendente
        return aprendizRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public void guardar(Aprendiz aprendiz) {
        // Validaciones de duplicados (solo si es nuevo)
        if (aprendiz.getId() == null) {
            if (aprendizRepository.existsByNumeroDocumento(aprendiz.getNumeroDocumento())) {
                throw new RuntimeException("El documento " + aprendiz.getNumeroDocumento() + " ya está registrado.");
            }
            if (aprendizRepository.existsByCorreo(aprendiz.getCorreo())) {
                throw new RuntimeException("El correo " + aprendiz.getCorreo() + " ya está registrado.");
            }
        }
        aprendizRepository.save(aprendiz);
    }

    @Override
    @Transactional(readOnly = true)
    public Aprendiz buscarPorId(Long id) {
        return aprendizRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        aprendizRepository.deleteById(id);
    }

    @Override
    public List<Aprendiz> buscarPorFicha(Long fichaId) {
        return aprendizRepository.findByFichaId(fichaId);
    }

    @Override
    public long totalAprendices() {
        return aprendizRepository.count();
    }
}