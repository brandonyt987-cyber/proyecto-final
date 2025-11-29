package com.sena.sistemaintegralsena.controllers;

import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sena.sistemaintegralsena.models.Usuario;
import com.sena.sistemaintegralsena.models.Rol;
import com.sena.sistemaintegralsena.repositories.RolRepository;
import com.sena.sistemaintegralsena.services.UsuarioService;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;

    // regex: 8+, mayúscula, minúscula, número, caracter especial
    private static final Pattern PWD_PATTERN =
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public AuthController(UsuarioService usuarioService, RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) model.addAttribute("error", "Usuario o contraseña inválidos.");
        if (logout != null) model.addAttribute("msg", "Sesión cerrada correctamente.");
        return "login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, Model model) {

        // validar correo no exista
        if (usuarioService.findByCorreo(usuario.getCorreo()).isPresent()) {
            model.addAttribute("error", "Ya existe un usuario con ese correo.");
            return "registro";
        }

        // validar contraseña
        if (!PWD_PATTERN.matcher(usuario.getContrasena()).matches()) {
            model.addAttribute("error", 
                "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial.");
            return "registro";
        }

        // asignar rol PSICOLOGA por defecto
        Rol rolPsico = rolRepository.findByNombre("PSICOLOGA")
                .orElse(null);

        if (rolPsico == null) {
            model.addAttribute("error", "Los roles no están inicializados. Reinicia la aplicación.");
            return "registro";
        }

        // encriptar contraseña
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setContrasena(encoder.encode(usuario.getContrasena()));

        // asignar rol único
        usuario.setRol(rolPsico);

        usuarioService.save(usuario);

        model.addAttribute("msg", "Registro correcto. Inicia sesión.");
        return "login";
    }
}
