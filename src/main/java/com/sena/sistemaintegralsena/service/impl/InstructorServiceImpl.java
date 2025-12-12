package com.sena.sistemaintegralsena.service.impl;

import com.sena.sistemaintegralsena.entity.Instructor;
import com.sena.sistemaintegralsena.repository.InstructorRepository;
import com.sena.sistemaintegralsena.service.InstructorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository repository;

    public InstructorServiceImpl(InstructorRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Instructor> listarTodos() {
        
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    @Transactional
    public void guardar(Instructor instructor) {
        
        if (instructor.getId() == null) {
            if (repository.existsByNumeroDocumento(instructor.getNumeroDocumento())) {
                throw new RuntimeException("El documento " + instructor.getNumeroDocumento() + " ya está registrado.");
            }
            if (repository.existsByCorreo(instructor.getCorreo())) {
                throw new RuntimeException("El correo " + instructor.getCorreo() + " ya está registrado.");
            }
        }
        repository.save(instructor);
    }

    @Override
    @Transactional(readOnly = true)
    public Instructor buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    
    @Override
    @Transactional
    public void cambiarEstado(Long id) {
        Instructor instructor = repository.findById(id).orElse(null);
        if (instructor != null) {
            instructor.setActivo(!instructor.isActivo()); 
            repository.save(instructor);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long totalInstructores() {
        return repository.count();
    }
}