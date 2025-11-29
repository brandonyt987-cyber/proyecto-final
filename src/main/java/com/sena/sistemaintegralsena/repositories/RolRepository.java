package com.sena.sistemaintegralsena.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sena.sistemaintegralsena.models.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
