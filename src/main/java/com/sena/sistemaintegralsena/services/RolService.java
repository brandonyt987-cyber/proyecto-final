package com.sena.sistemaintegralsena.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sena.sistemaintegralsena.models.Rol;
import com.sena.sistemaintegralsena.repositories.RolRepository;

@Service
public class RolService {

    private final RolRepository repo;

    public RolService(RolRepository repo) {
        this.repo = repo;
    }

    // Listar todos los roles
    public List<Rol> listar() {
        return repo.findAll();
    }

    // Obtener un rol por ID
    public Rol obtenerPorId(Long id) {
        Optional<Rol> r = repo.findById(id);
        return r.orElse(null);
    }

    // Guardar rol (por si lo necesitas)
    public Rol guardar(Rol rol) {
        return repo.save(rol);
    }
}
