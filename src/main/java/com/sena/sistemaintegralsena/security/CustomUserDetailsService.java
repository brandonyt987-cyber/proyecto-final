package com.sena.sistemaintegralsena.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sena.sistemaintegralsena.models.Usuario;
import com.sena.sistemaintegralsena.repositories.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public CustomUserDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Optional<Usuario> opt = repo.findByCorreo(correo);
        if (opt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + correo);
        }
        Usuario usuario = opt.get();
        return new CustomUserDetails(usuario);
    }
}
