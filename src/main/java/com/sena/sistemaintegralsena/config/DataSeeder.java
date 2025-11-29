package com.sena.sistemaintegralsena.config;

import com.sena.sistemaintegralsena.entity.Rol;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.repository.RolRepository;
import com.sena.sistemaintegralsena.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataSeeder {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Método auxiliar para obtener o crear un rol
    private Rol findOrCreateRol(String nombre) {
        Optional<Rol> rolOpt = rolRepository.findByNombre(nombre);
        if (rolOpt.isPresent()) {
            return rolOpt.get();
        }
        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(nombre);
        return rolRepository.save(nuevoRol);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedData() {

        // ============================
        // 1. CREACIÓN DE ROLES (CRÍTICO para evitar el error 500)
        // ============================
        // Crea las filas en la tabla 'rol' antes de que los usuarios las referencien.
        Rol rolAdmin = findOrCreateRol("ADMIN");
        findOrCreateRol("PSICOLOGA");
        findOrCreateRol("T_SOCIAL");


        // ============================
        // 2. CREACIÓN DE USUARIOS ADMINISTRADORES (Dos usuarios)
        // ============================

        // --- Administrador 1 ---
        if (!usuarioRepository.existsByEmail("admin1@sena.edu.co")) {

            Usuario admin1 = new Usuario();
            admin1.setNombre("Administrador Principal");
            admin1.setEmail("admin1@sena.edu.co");
            admin1.setPassword(passwordEncoder.encode("Nala123*"));
            admin1.setEnabled(true);

            // Asignamos el nombre del rol simple (ADMIN)
            admin1.setRol(rolAdmin.getNombre()); 

            usuarioRepository.save(admin1);
        }
        
        // --- Administrador 2 ---
        if (!usuarioRepository.existsByEmail("admin2@sena.edu.co")) {

            Usuario admin2 = new Usuario();
            admin2.setNombre("Administrador Secundario");
            admin2.setEmail("admin2@sena.edu.co");
            admin2.setPassword(passwordEncoder.encode("Nala123*"));
            admin2.setEnabled(true);

            // Asignamos el nombre del rol simple (ADMIN)
            admin2.setRol(rolAdmin.getNombre());

            usuarioRepository.save(admin2);
        }
    }
}