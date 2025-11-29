package com.sena.sistemaintegralsena.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sena.sistemaintegralsena.models.Rol;
import com.sena.sistemaintegralsena.models.Usuario;
import com.sena.sistemaintegralsena.repositories.RolRepository;
import com.sena.sistemaintegralsena.repositories.UsuarioRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UsuarioRepository userRepo, RolRepository rolRepo) {
        return args -> {

            // Crear roles si no existen
            if (rolRepo.count() == 0) {
                rolRepo.save(new Rol("ADMIN"));
                rolRepo.save(new Rol("PSICOLOGA"));
            }

            // Crear usuarios iniciales
            if (userRepo.count() == 0) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                Rol rolAdmin = rolRepo.findByNombre("ADMIN").orElse(null);
                Rol rolPsico = rolRepo.findByNombre("PSICOLOGA").orElse(null);

                // Seguridad: si por algún motivo no se guardaron roles
                if (rolAdmin == null || rolPsico == null) {
                    System.out.println("❌ ERROR: No se pudieron cargar roles iniciales.");
                    return;
                }

                // === ADMIN ===
                Usuario admin = new Usuario();
                admin.setNombreCompleto("Administrador Principal");
                admin.setDocumento("1000001");
                admin.setCorreo("admin@sena.edu.co");
                admin.setContrasena(encoder.encode("Admin123*"));
                admin.setRol(rolAdmin);
                userRepo.save(admin);

                // === PSICOLOGA ===
                Usuario psico = new Usuario();
                psico.setNombreCompleto("Psicóloga Ejemplo");
                psico.setDocumento("2000001");
                psico.setCorreo("psico@sena.edu.co");
                psico.setContrasena(encoder.encode("Psico123*"));
                psico.setRol(rolPsico);
                userRepo.save(psico);

                System.out.println("✔ Usuarios iniciales creados correctamente.");
            }
        };
    }
}
