package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Atencion;
import java.util.List;

public interface AtencionService {
    List<Atencion> listarTodas();
    List<Atencion> listarPorProfesional(Long usuarioId);
    void guardar(Atencion atencion, Long aprendizId, String emailProfesional);
    Atencion buscarPorId(Long id);
    
    
    void cambiarEstado(Long id);
}