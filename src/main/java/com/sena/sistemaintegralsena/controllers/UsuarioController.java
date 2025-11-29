package com.sena.sistemaintegralsena.controllers;

import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.sena.sistemaintegralsena.models.Rol;
import com.sena.sistemaintegralsena.models.Usuario;
import com.sena.sistemaintegralsena.services.RolService;
import com.sena.sistemaintegralsena.services.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;

    private static final Pattern NOMBRE_PATTERN =
        Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúñÑ ]{5,100}$");

    private static final Pattern DOCUMENTO_PATTERN =
        Pattern.compile("^[0-9]{5,}$");

    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @GetMapping("/crear")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolService.listar());  // <--- AQUÍ EL ARREGLO
        return "usuarios/crear";
    }

    @PostMapping("/crear")
    public String crearUsuario(
            @RequestParam String nombreCompleto,
            @RequestParam String documento,
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam String confirmarContrasena,
            @RequestParam Long rolId,
            Model model
    ) {

        // Validar nombre
        if (!NOMBRE_PATTERN.matcher(nombreCompleto).matches()) {
            model.addAttribute("error",
                    "El nombre debe tener entre 5 y 100 caracteres; solo letras y espacios.");
            model.addAttribute("roles", rolService.listar());
            return "usuarios/crear";
        }

        // Validar documento
        if (!DOCUMENTO_PATTERN.matcher(documento).matches()) {
            model.addAttribute("error",
                    "El documento debe tener mínimo 5 dígitos, solo números.");
            model.addAttribute("roles", rolService.listar());
            return "usuarios/crear";
        }

        // Validar correo único
        if (usuarioService.findByCorreo(correo).isPresent()) {
            model.addAttribute("error", "El correo ya existe.");
            model.addAttribute("roles", rolService.listar());
            return "usuarios/crear";
        }

        // Validar contraseñas
        if (!PASSWORD_PATTERN.matcher(contrasena).matches()) {
            model.addAttribute("error",
                "La contraseña debe tener mínimo 8 caracteres, mayúscula, minúscula, número y caracter especial.");
            model.addAttribute("roles", rolService.listar());
            return "usuarios/crear";
        }

        if (!contrasena.equals(confirmarContrasena)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            model.addAttribute("roles", rolService.listar());
            return "usuarios/crear";
        }

        // Buscar rol
        Rol rol = rolService.obtenerPorId(rolId);
        if (rol == null) {
            model.addAttribute("error", "El rol seleccionado no existe.");
            model.addAttribute("roles", rolService.listar());
            return "usuarios/crear";
        }

        // Crear usuario
        Usuario u = new Usuario();
        u.setNombreCompleto(nombreCompleto);
        u.setDocumento(documento);
        u.setCorreo(correo);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        u.setContrasena(encoder.encode(contrasena));

        u.setRol(rol);

        usuarioService.save(u);

        model.addAttribute("msg", "Usuario creado correctamente.");
        return "redirect:/usuarios/crear";
    }
}
