package com.sena.sistemaintegralsena.security;

import com.sena.sistemaintegralsena.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // ¡IMPORTANTE! Spring lo inyecta como el servicio de detalles de usuario
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // Usa el método que confirmamos que existe en el repositorio
        return usuarioRepository.findByEmail(email)
                .map(UsuarioPrincipal::build)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }
}