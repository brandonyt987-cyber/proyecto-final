package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Comite;
import java.util.List;

public interface ComiteService {
    
    List<Comite> listarTodos();
    
    // Guardar requiere el objeto, el ID del aprendiz y el email del profesional logueado
    void guardar(Comite comite, Long aprendizId, String emailProfesional);
    
    Comite buscarPorId(Long id);
    
    void eliminar(Long id);
}