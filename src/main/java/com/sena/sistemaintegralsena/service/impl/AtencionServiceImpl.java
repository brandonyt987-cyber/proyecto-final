package com.sena.sistemaintegralsena.service.impl;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import com.sena.sistemaintegralsena.entity.Atencion;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.repository.AprendizRepository;
import com.sena.sistemaintegralsena.repository.AtencionRepository;
import com.sena.sistemaintegralsena.repository.UsuarioRepository;
import com.sena.sistemaintegralsena.service.AtencionService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AtencionServiceImpl implements AtencionService {

    private final AtencionRepository atencionRepository;
    private final AprendizRepository aprendizRepository;
    private final UsuarioRepository usuarioRepository;

    public AtencionServiceImpl(AtencionRepository atencionRepository, 
                               AprendizRepository aprendizRepository,
                               UsuarioRepository usuarioRepository) {
        this.atencionRepository = atencionRepository;
        this.aprendizRepository = aprendizRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Atencion> listarTodas() {
        return atencionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public List<Atencion> listarPorProfesional(Long usuarioId) {
        return atencionRepository.findByProfesionalId(usuarioId);
    }

    @Override
    public void guardar(Atencion atencion, Long aprendizId, String emailProfesional) {
        
        // 1. ASOCIAR APRENDIZ
        Aprendiz aprendiz = aprendizRepository.findById(aprendizId)
                .orElseThrow(() -> new RuntimeException("Aprendiz no encontrado"));
        atencion.setAprendiz(aprendiz);

        // 2. ASOCIAR PROFESIONAL (CORRECCIÓN AQUÍ 👇)
        
        // Caso A: Viene del SELECT en el formulario (Tiene ID)
        if (atencion.getProfesional() != null && atencion.getProfesional().getId() != null) {
            Usuario profesional = usuarioRepository.findById(atencion.getProfesional().getId())
                    .orElseThrow(() -> new RuntimeException("El profesional seleccionado no existe en la base de datos."));
            atencion.setProfesional(profesional);
        } 
        // Caso B: No viene del formulario, usamos el email del usuario logueado (Respaldo)
        else if (emailProfesional != null) {
            Usuario profesional = usuarioRepository.findByEmail(emailProfesional)
                    .orElseThrow(() -> new RuntimeException("Profesional no encontrado por email."));
            atencion.setProfesional(profesional);
        } 
        // Caso C: Error, no hay profesional
        else {
            throw new RuntimeException("Debe seleccionar un profesional a cargo.");
        }

        atencionRepository.save(atencion);
    }

    @Override
    @Transactional(readOnly = true)
    public Atencion buscarPorId(Long id) {
        return atencionRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        atencionRepository.deleteById(id);
    }
}