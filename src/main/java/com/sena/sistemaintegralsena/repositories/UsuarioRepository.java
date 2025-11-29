package com.sena.sistemaintegralsena.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sena.sistemaintegralsena.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByDocumento(String documento);
}
