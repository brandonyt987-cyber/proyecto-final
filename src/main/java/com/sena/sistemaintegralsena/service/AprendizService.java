package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import java.util.List;

public interface AprendizService {
    
    List<Aprendiz> listarTodos();
    
    void guardar(Aprendiz aprendiz);
    
    Aprendiz buscarPorId(Long id);
    
    void eliminar(Long id);
    
    List<Aprendiz> buscarPorFicha(Long fichaId);
    
    // 🔑 MÉTODO NECESARIO
    long totalAprendices();
}