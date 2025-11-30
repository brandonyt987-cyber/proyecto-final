package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Taller;
import java.util.List;

public interface TallerService {
    List<Taller> listarTodos();
    void guardar(Taller taller);
    Taller buscarPorId(Long id);
    void eliminar(Long id);
}