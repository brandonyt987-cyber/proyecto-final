package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import java.util.List;

public interface AprendizService {
    
    List<Aprendiz> listarTodos();
    
    void guardar(Aprendiz aprendiz);
    
    Aprendiz buscarPorId(Long id);
    
    
    void cambiarEstado(Long id);
    
    List<Aprendiz> buscarPorFicha(Long fichaId);
    
    Aprendiz buscarPorDocumento(String documento); 
    
    long totalAprendices();
}