package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.dto.UsuarioRegistroDTO;
import com.sena.sistemaintegralsena.entity.Rol;
import com.sena.sistemaintegralsena.repository.RolRepository;
import com.sena.sistemaintegralsena.service.UsuarioService;
import com.sena.sistemaintegralsena.exceptions.EmailExistenteException; // Importación corregida

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    public AuthController(UsuarioService usuarioService, RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
    }

    // Roles permitidos en el formulario de REGISTRO (Excluye ADMIN)
    private static final List<String> ROLES_PERMITIDOS = List.of("PSICOLOGA", "T_SOCIAL");

    // Método para cargar solo los roles permitidos en el modelo
    private void cargarRolesRegistro(Model model) {
        List<Rol> rolesDisponibles = rolRepository.findAll()
                .stream()
                .filter(rol -> ROLES_PERMITIDOS.contains(rol.getNombre()))
                .collect(Collectors.toList());
        
        model.addAttribute("roles", rolesDisponibles);
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDTO());
        cargarRolesRegistro(model); // Carga solo roles permitidos
        return "registro";
    }

    @PostMapping("/registro/guardar")
    public String registrarUsuario(@Valid @ModelAttribute("usuario") UsuarioRegistroDTO registroDTO,
                                     BindingResult result, // Captura errores del DTO
                                     Model model,
                                     RedirectAttributes redirectAttributes) {

        // 1. CHEQUEO DE ERRORES DE VALIDACIÓN (DTO, incluyendo PasswordMatches)
        if (result.hasErrors()) {
            cargarRolesRegistro(model);
            return "registro"; // Vuelve al formulario para mostrar errores de campo
        }

        try {
            // Buscamos el nombre del rol usando el ID
            String rolNombre = rolRepository.findById(registroDTO.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado")).getNombre();

            // 2. Lógica de Guardado (lanza EmailExistenteException si ya existe)
            usuarioService.guardarNuevoUsuario(registroDTO, rolNombre); 

        } catch (EmailExistenteException e) {
            // 3. Maneja el error de email duplicado añadiéndolo a BindingResult
            // Esto permite que el error aparezca junto a un campo específico (ej. email)
            result.rejectValue("email", "error.usuario", e.getMessage());
            
            cargarRolesRegistro(model);
            return "registro"; // Vuelve al formulario con el error
        } catch (RuntimeException e) {
            // Maneja otros errores (ej. Rol no encontrado)
            model.addAttribute("errorGlobal", "Error al procesar la solicitud: " + e.getMessage());
            cargarRolesRegistro(model);
            return "registro";
        }

        // 4. REGISTRO EXITOSO: Usar addFlashAttribute para el mensaje de éxito después de la redirección
        redirectAttributes.addFlashAttribute("registroExitoso", "¡Usuario registrado con éxito! Ahora puedes iniciar sesión.");
        return "redirect:/login"; 
    }
    
    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }
}