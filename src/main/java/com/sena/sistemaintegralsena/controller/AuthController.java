package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.dto.UsuarioRegistroDTO;
import com.sena.sistemaintegralsena.entity.PasswordResetToken;
import com.sena.sistemaintegralsena.entity.Rol;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.exceptions.EmailExistenteException;
import com.sena.sistemaintegralsena.repository.PasswordResetTokenRepository;
import com.sena.sistemaintegralsena.repository.RolRepository;
import com.sena.sistemaintegralsena.repository.UsuarioRepository;
import com.sena.sistemaintegralsena.service.EmailService;
import com.sena.sistemaintegralsena.service.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; 
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private EmailService emailService;
    @Autowired private PasswordEncoder passwordEncoder;

    public AuthController(UsuarioService usuarioService, RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
    }

    // --- MÉTODOS DE LOGIN Y REGISTRO ---

    private static final List<String> ROLES_PERMITIDOS = List.of("PSICOLOGA", "T_SOCIAL");

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
        cargarRolesRegistro(model);
        return "registro";
    }

    @PostMapping("/registro/guardar")
    public String registrarUsuario(@Valid @ModelAttribute("usuario") UsuarioRegistroDTO registroDTO,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.usuario", "Las contraseñas no coinciden.");
        }

        if (result.hasErrors()) {
            cargarRolesRegistro(model);
            return "registro";
        }

        try {
            String rolNombre = rolRepository.findById(registroDTO.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado")).getNombre();

            usuarioService.guardarNuevoUsuario(registroDTO, rolNombre); 

        } catch (EmailExistenteException e) {
            result.rejectValue("email", "error.usuario", e.getMessage());
            cargarRolesRegistro(model);
            return "registro";
        } catch (RuntimeException e) {
            model.addAttribute("errorGlobal", "Error al procesar la solicitud: " + e.getMessage());
            cargarRolesRegistro(model);
            return "registro";
        }

        redirectAttributes.addFlashAttribute("registroExitoso", "¡Usuario registrado con éxito! Ahora puedes iniciar sesión.");
        return "redirect:/login"; 
    }
    
    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    // --- MÉTODOS DE RECUPERACIÓN DE CONTRASEÑA ---

    @GetMapping("/recuperar-password")
    public String vistaRecuperar() {
        return "recuperar_password";
    }

    @PostMapping("/enviar-recuperacion")
    public String procesarRecuperacion(@RequestParam("email") String email, RedirectAttributes redirect) {
        
        // 1. Validar vacío (Para que salga el mensaje rojo en el HTML)
        if (email == null || email.trim().isEmpty()) {
            redirect.addFlashAttribute("error", "El correo institucional es obligatorio.");
            return "redirect:/recuperar-password";
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String token = UUID.randomUUID().toString();
            
            PasswordResetToken resetToken = new PasswordResetToken(token, usuario);
            tokenRepository.save(resetToken);

            try {
                emailService.enviarCorreoRecuperacion(email, token);
                redirect.addFlashAttribute("registroExitoso", "Se ha enviado un enlace de recuperación a tu correo.");
            } catch (Exception e) {
                redirect.addFlashAttribute("error", "Error al enviar el correo. Verifica tu configuración.");
            }
        } else {
            // Mensaje genérico por seguridad
            redirect.addFlashAttribute("registroExitoso", "Si el correo existe, recibirás las instrucciones.");
        }
        return "redirect:/login";
    }

    @GetMapping("/restaurar-password")
    public String vistaRestaurar(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().estaVencido()) {
            model.addAttribute("error", "El enlace es inválido o ha expirado.");
            return "login";
        }

        model.addAttribute("token", token);
        return "restaurar_password";
    }

    @PostMapping("/guardar-password")
    @Transactional
    public String guardarPassword(@RequestParam("token") String token, 
                                  @RequestParam("password") String password,
                                  @RequestParam("confirmPassword") String confirmPassword,
                                  Model model, 
                                  RedirectAttributes redirect) {
        
        // 1. Validar el token primero
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().estaVencido()) {
            redirect.addFlashAttribute("error", "El enlace ha caducado o es inválido.");
            return "redirect:/login";
        }

        // 2. Validar campos vacíos
        if (password == null || password.trim().isEmpty() || confirmPassword == null || confirmPassword.trim().isEmpty()) {
            model.addAttribute("error", "Todos los campos son obligatorios.");
            model.addAttribute("token", token); // Devolver el token para no perder la sesión
            return "restaurar_password";
        }

        // 3. Validar coincidencia
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            model.addAttribute("token", token);
            return "restaurar_password";
        }

        // 4. Validar Seguridad Estricta (Min 8, Mayús, Minús, Símbolo)
        if (password.length() < 8) {
            model.addAttribute("error", "La contraseña debe tener al menos 8 caracteres.");
            model.addAttribute("token", token);
            return "restaurar_password";
        }
        if (!password.matches(".*[A-Z].*")) {
            model.addAttribute("error", "La contraseña debe tener al menos una letra Mayúscula.");
            model.addAttribute("token", token);
            return "restaurar_password";
        }
        if (!password.matches(".*[a-z].*")) {
            model.addAttribute("error", "La contraseña debe tener al menos una letra minúscula.");
            model.addAttribute("token", token);
            return "restaurar_password";
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            model.addAttribute("error", "La contraseña debe incluir al menos un carácter especial (!@#$...).");
            model.addAttribute("token", token);
            return "restaurar_password";
        }

        // 5. Guardar cambio si todo está bien
        Usuario usuario = tokenOpt.get().getUsuario();
        usuario.setPassword(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);

        tokenRepository.deleteByToken(token);

        redirect.addFlashAttribute("registroExitoso", "¡Contraseña actualizada correctamente!");
        return "redirect:/login";
    }
}