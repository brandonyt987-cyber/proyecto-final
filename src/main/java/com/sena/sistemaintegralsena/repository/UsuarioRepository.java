package com.sena.sistemaintegralsena.repository;

import com.sena.sistemaintegralsena.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection; // <--- Importación necesaria
import java.util.List;       // <--- Importación necesaria
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);

    List<Usuario> findByRolIn(Collection<String> roles);
}