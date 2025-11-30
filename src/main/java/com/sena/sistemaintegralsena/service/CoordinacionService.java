package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Coordinacion;
import java.util.List;

public interface CoordinacionService {
    List<Coordinacion> listarTodas();
    void guardar(Coordinacion coordinacion);
    Coordinacion buscarPorId(Long id);
    void eliminar(Long id);
    long totalCoordinaciones();
}