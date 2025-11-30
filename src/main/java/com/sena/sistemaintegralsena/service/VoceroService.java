package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Vocero;
import java.util.List;

public interface VoceroService {
    List<Vocero> listarTodos();
    void guardar(Vocero vocero);
    Vocero buscarPorId(Long id);
    void eliminar(Long id);
    
    // Para el Dashboard
    long totalVoceros();
}