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
    private final RolRepository rolRepository; // Necesario para buscar el nombre del rol al editar
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

    // 🆕 IMPLEMENTACIÓN DE ACTUALIZAR
    @Override
    public void actualizarUsuarioDesdeDTO(UsuarioEdicionDTO dto) throws EmailExistenteException {
        Usuario usuarioActual = usuarioRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar unicidad de email (solo si cambió)
        if (!usuarioActual.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new EmailExistenteException("El email " + dto.getEmail() + " ya está en uso.");
        }

        usuarioActual.setNombre(dto.getNombre());
        usuarioActual.setEmail(dto.getEmail());

        // Actualizar Rol
        Rol rol = rolRepository.findById(dto.getRolId()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuarioActual.setRol(rol.getNombre());

        // Actualizar Contraseña solo si viene con datos
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuarioActual.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        usuarioRepository.save(usuarioActual);
    }

    @Override
    public List<Usuario> listarTodos() { return usuarioRepository.findAll(Sort.by(Sort.Direction.ASC, "id")); }

    @Override
    public Usuario buscarPorId(Long id) { return usuarioRepository.findById(id).orElse(null); }

    @Override
    public void eliminar(Long id) { usuarioRepository.deleteById(id); }

    @Override
    public Long totalUsuarios() { return usuarioRepository.count(); }
}