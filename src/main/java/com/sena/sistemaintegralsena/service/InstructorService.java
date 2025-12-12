package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Instructor;
import java.util.List;

public interface InstructorService {
    List<Instructor> listarTodos();
    void guardar(Instructor instructor);
    Instructor buscarPorId(Long id);
    void cambiarEstado(Long id); 
    long totalInstructores();
}