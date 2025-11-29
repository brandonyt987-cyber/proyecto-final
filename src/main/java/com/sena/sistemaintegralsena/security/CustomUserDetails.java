package com.sena.sistemaintegralsena.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sena.sistemaintegralsena.models.Usuario;

public class CustomUserDetails implements UserDetails {

    private final Usuario user;

    public CustomUserDetails(Usuario user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Como ahora SOLO tienes 1 rol
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRol().getNombre()));
    }

    @Override
    public String getPassword() {
        return user.getContrasena();  // CAMBIO
    }

    @Override
    public String getUsername() {
        return user.getCorreo();  // Esto sí estaba bien
    }

    @Override public boolean isAccountNonExpired() { return true; }

    @Override public boolean isAccountNonLocked() { return true; }

    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override 
    public boolean isEnabled() { 
        return true; // ya NO tienes campo "activo"
    }

    public String getNombreCompleto() {
        return user.getNombreCompleto(); // CAMBIO
    }
}
