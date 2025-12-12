package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.dto.UsuarioEdicionDTO;
import com.sena.sistemaintegralsena.dto.UsuarioRegistroDTO;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.exceptions.EmailExistenteException;
import com.sena.sistemaintegralsena.repository.RolRepository;
import com.sena.sistemaintegralsena.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    public UsuarioController(UsuarioService usuarioService, RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/lista"; 
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioRegistroDTO());
        model.addAttribute("roles", rolRepository.findAll());
        return "usuarios/crear"; 
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuarioDTO") UsuarioRegistroDTO registroDTO,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.usuario", "Las contraseñas no coinciden");
        }

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
            result.rejectValue("email", "error.usuario", e.getMessage());
            model.addAttribute("roles", rolRepository.findAll());
            return "usuarios/crear";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        if (usuario == null) return "redirect:/usuarios";

        UsuarioEdicionDTO dto = new UsuarioEdicionDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        
        rolRepository.findByNombre(usuario.getRol())
            .ifPresent(rol -> dto.setRolId(rol.getId()));

        model.addAttribute("usuarioEdicion", dto);
        model.addAttribute("roles", rolRepository.findAll());
        return "usuarios/editar"; 
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

    
    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.cambiarEstado(id, principal.getName());
            redirectAttributes.addFlashAttribute("exito", "Estado del usuario actualizado.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}