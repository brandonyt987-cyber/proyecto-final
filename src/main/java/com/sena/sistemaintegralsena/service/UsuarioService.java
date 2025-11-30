package com.sena.sistemaintegralsena.service;

import com.sena.sistemaintegralsena.dto.UsuarioEdicionDTO; // Necesitas importar este nuevo DTO
import com.sena.sistemaintegralsena.dto.UsuarioRegistroDTO;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.exceptions.EmailExistenteException;
import java.util.List;

public interface UsuarioService {

    void guardarNuevoUsuario(UsuarioRegistroDTO registroDTO, String rolNombre) throws EmailExistenteException;

    // 🆕 NUEVO MÉTODO PARA EDITAR
    void actualizarUsuarioDesdeDTO(UsuarioEdicionDTO edicionDTO) throws EmailExistenteException;

    List<Usuario> listarTodos();
    Usuario buscarPorId(Long id);
    void eliminar(Long id);
    Long totalUsuarios();
}