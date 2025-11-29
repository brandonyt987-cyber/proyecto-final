package com.sena.sistemaintegralsena.services;

import java.util.Optional;
import org.springframework.stereotype.Service;

import com.sena.sistemaintegralsena.models.Usuario;
import com.sena.sistemaintegralsena.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Optional<Usuario> findByCorreo(String correo) {
        return repo.findByCorreo(correo);
    }

    public Optional<Usuario> findByDocumento(String documento) {
        return repo.findByDocumento(documento);
    }

    public Usuario save(Usuario u) {
        return repo.save(u);
    }

    public long count() {
        return repo.count();
    }
}
