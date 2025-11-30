package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Atencion;
import java.util.List;

public interface AtencionService {
    List<Atencion> listarTodas();
    List<Atencion> listarPorProfesional(Long usuarioId); // Para ver solo mis casos
    void guardar(Atencion atencion, Long aprendizId, String emailProfesional);
    Atencion buscarPorId(Long id);
    void eliminar(Long id);
}