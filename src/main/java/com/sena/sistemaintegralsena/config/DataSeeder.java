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
        // 1. CREACIÓN DE ROLES (CRÍTICO)
        // ============================
        Rol rolAdmin = findOrCreateRol("ADMIN");
        Rol rolPsico = findOrCreateRol("PSICOLOGA");
        Rol rolSocial = findOrCreateRol("T_SOCIAL");


        // ============================
        // 2. CREACIÓN DE USUARIOS INICIALES
        // ============================

        // --- Administrador unico ---
        if (!usuarioRepository.existsByEmail("v64149378@gmail.com")) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador Principal");
            admin.setEmail("v64149378@gmail.com");
            admin.setPassword(passwordEncoder.encode("Nala123*"));
            admin.setEnabled(true);
            admin.setRol(rolAdmin.getNombre()); 
            usuarioRepository.save(admin);
            System.out.println(">> Usuario ADMIN creado: v64149378@gmail.com");
        }
        
        // --- Pisicologa ---
        if (!usuarioRepository.existsByEmail("psico@sena.edu.co")) {
            Usuario psico = new Usuario();
            psico.setNombre("Laura Gómez");
            psico.setEmail("psico@sena.edu.co");
            psico.setPassword(passwordEncoder.encode("Nala123*"));
            psico.setEnabled(true);
            psico.setRol(rolPsico.getNombre());
            usuarioRepository.save(psico);
            System.out.println(">> Usuario PSICOLOGA creado: psico@sena.edu.co");
        }

        // --- Trabajadora Social ---
        if (!usuarioRepository.existsByEmail("social@sena.edu.co")) {
            Usuario social = new Usuario();
            social.setNombre("María Torres");
            social.setEmail("social@sena.edu.co");
            social.setPassword(passwordEncoder.encode("Nala123*"));
            social.setEnabled(true);
            social.setRol(rolSocial.getNombre());
            usuarioRepository.save(social);
            System.out.println(">> Usuario T_SOCIAL creado: social@sena.edu.co");
        }
    }
}