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

import java.time.LocalDateTime;
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
        return atencionRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public List<Atencion> listarPorProfesional(Long usuarioId) {
        return atencionRepository.findByProfesionalId(usuarioId);
    }

    @Override
    public void guardar(Atencion atencion, Long aprendizId, String emailProfesional) {
        
        // --- 1. LÓGICA DE BLINDAJE DE FECHA (AQUÍ ESTABA EL ERROR) ---
        if (atencion.getId() != null) {
            
            Atencion original = atencionRepository.findById(atencion.getId()).orElse(null);
            
            if (original != null) {
                
                atencion.setFechaCreacionRegistro(original.getFechaCreacionRegistro());
                
                
                if (atencion.getFechaCreacionRegistro() == null) {
                    atencion.setFechaCreacionRegistro(LocalDateTime.now());
                }
            }
        } else {
            
            if (atencion.getFechaCreacionRegistro() == null) {
                atencion.setFechaCreacionRegistro(LocalDateTime.now());
            }
        }

        // --- 2. ASOCIAR APRENDIZ ---
        Aprendiz aprendiz = aprendizRepository.findById(aprendizId)
                .orElseThrow(() -> new RuntimeException("Aprendiz no encontrado"));
        atencion.setAprendiz(aprendiz);

        // --- 3. ASOCIAR PROFESIONAL ---
        if (atencion.getProfesional() != null && atencion.getProfesional().getId() != null) {
            Usuario profesional = usuarioRepository.findById(atencion.getProfesional().getId())
                    .orElseThrow(() -> new RuntimeException("El profesional seleccionado no existe."));
            atencion.setProfesional(profesional);
        } 
        else if (emailProfesional != null) {
            Usuario profesional = usuarioRepository.findByEmail(emailProfesional)
                    .orElseThrow(() -> new RuntimeException("Profesional no encontrado por email."));
            atencion.setProfesional(profesional);
        } 
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
    public void cambiarEstado(Long id) {
        Atencion atencion = atencionRepository.findById(id).orElse(null);
        if (atencion != null) {
            atencion.setActivo(!atencion.isActivo());
            atencionRepository.save(atencion);
        }
    }
}