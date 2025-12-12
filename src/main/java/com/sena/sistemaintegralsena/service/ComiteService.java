package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Comite;
import java.util.List;

public interface ComiteService {
    
    List<Comite> listarTodos();
    
    void guardar(Comite comite, Long aprendizId, String emailProfesional);
    
    Comite buscarPorId(Long id);
    
    
    void cambiarEstado(Long id);
}