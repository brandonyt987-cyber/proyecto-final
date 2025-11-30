package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.dto.UsuarioEdicionDTO;
import com.sena.sistemaintegralsena.dto.UsuarioRegistroDTO;
import com.sena.sistemaintegralsena.entity.Rol;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.exceptions.EmailExistenteException;
import com.sena.sistemaintegralsena.repository.RolRepository;
import com.sena.sistemaintegralsena.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    public UsuarioController(UsuarioService usuarioService, RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
    }

    // ==========================================
    // 1. LISTAR USUARIOS
    // ==========================================
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista"; // Retorna la vista lista.html
    }

    // ==========================================
    // 2. CREAR USUARIO (Vista y Acción)
    // ==========================================
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioRegistroDTO());
        model.addAttribute("roles", rolRepository.findAll());
        return "usuarios/crear"; // Retorna crear.html
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuarioDTO") UsuarioRegistroDTO registroDTO,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("roles", rolRepository.findAll());
            return "usuarios/crear";
        }

        try {
            String rolNombre = rolRepository.findById(registroDTO.getRolId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado")).getNombre();
            
            usuarioService.guardarNuevoUsuario(registroDTO, rolNombre);
            redirectAttributes.addFlashAttribute("exito", "Usuario creado correctamente.");
            
        } catch (EmailExistenteException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", rolRepository.findAll());
            return "usuarios/crear";
        }
        return "redirect:/usuarios";
    }

    // ==========================================
    // 3. EDITAR USUARIO (Vista y Acción)
    // ==========================================
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) return "redirect:/usuarios";

        // Mapear Entidad -> DTO Edición
        UsuarioEdicionDTO dto = new UsuarioEdicionDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        
        // Buscar ID del rol actual
        rolRepository.findByNombre(usuario.getRol())
            .ifPresent(rol -> dto.setRolId(rol.getId()));

        model.addAttribute("usuarioEdicion", dto);
        model.addAttribute("roles", rolRepository.findAll());
        return "usuarios/editar"; // Retorna editar.html
    }

    @PostMapping("/actualizar")
    public String actualizarUsuario(@Valid @ModelAttribute("usuarioEdicion") UsuarioEdicionDTO edicionDTO,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("roles", rolRepository.findAll());
            return "usuarios/editar";
        }

        try {
            usuarioService.actualizarUsuarioDesdeDTO(edicionDTO);
            redirectAttributes.addFlashAttribute("exito", "Usuario actualizado correctamente.");
            
        } catch (EmailExistenteException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", rolRepository.findAll());
            return "usuarios/editar";
        }
        return "redirect:/usuarios";
    }

    // ==========================================
    // 4. ELIMINAR USUARIO
    // ==========================================
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usuarioService.eliminar(id);
        redirectAttributes.addFlashAttribute("exito", "Usuario eliminado correctamente.");
        return "redirect:/usuarios";
    }
}