package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.entity.Ficha;
import java.util.List;

public interface FichaService {
    
    List<Ficha> listarTodas();
    
    void guardar(Ficha ficha);
    
    Ficha buscarPorId(Long id);
    
    void eliminar(Long id);
    
    // 🔑 ESTE ES EL MÉTODO QUE FALTABA
    long totalFichas();
}