package com.sena.sistemaintegralsena.service.impl;

import com.sena.sistemaintegralsena.dto.UsuarioEdicionDTO;
import com.sena.sistemaintegralsena.dto.UsuarioRegistroDTO;
import com.sena.sistemaintegralsena.entity.Rol;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.exceptions.EmailExistenteException;
import com.sena.sistemaintegralsena.repository.RolRepository;
import com.sena.sistemaintegralsena.repository.UsuarioRepository;
import com.sena.sistemaintegralsena.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import java.util.List;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository; 
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void guardarNuevoUsuario(UsuarioRegistroDTO registroDTO, String rolNombre) throws EmailExistenteException {
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new EmailExistenteException("El email " + registroDTO.getEmail() + " ya existe.");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        usuario.setRol(rolNombre);
        usuario.setEnabled(true); 
        usuarioRepository.save(usuario);
    }

    @Override
    public void actualizarUsuarioDesdeDTO(UsuarioEdicionDTO dto) throws EmailExistenteException {
        Usuario usuarioActual = usuarioRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuarioActual.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailExistenteException("El email " + dto.getEmail() + " ya está en uso.");
        }

        usuarioActual.setNombre(dto.getNombre());
        usuarioActual.setEmail(dto.getEmail());

        Rol rol = rolRepository.findById(dto.getRolId()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuarioActual.setRol(rol.getNombre());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuarioActual.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        usuarioRepository.save(usuarioActual);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() { 
        return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id")); 
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) { 
        return usuarioRepository.findById(id).orElse(null); 
    }

    
    @Override
    public void cambiarEstado(Long id, String emailSolicitante) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null) {
            
            if (usuario.getEmail().equals(emailSolicitante)) {
                throw new RuntimeException("No puedes desactivar tu propia cuenta.");
            }
            usuario.setEnabled(!usuario.isEnabled());
            usuarioRepository.save(usuario);
        }
    }

    @Override
    public Long totalUsuarios() { return usuarioRepository.count(); }
}