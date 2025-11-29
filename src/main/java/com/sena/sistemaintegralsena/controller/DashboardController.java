package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.service.AprendizService; // Importar
import com.sena.sistemaintegralsena.service.FichaService;   // Importar
import com.sena.sistemaintegralsena.service.UsuarioService; // Importar

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.security.Principal;

@Controller
public class DashboardController {

    private final UsuarioService usuarioService;
    private final FichaService fichaService;     // Nuevo atributo
    private final AprendizService aprendizService; // Nuevo atributo

    // Inyectar los 3 servicios
    public DashboardController(UsuarioService usuarioService, 
                               FichaService fichaService,
                               AprendizService aprendizService) {
        this.usuarioService = usuarioService;
        this.fichaService = fichaService;
        this.aprendizService = aprendizService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        // 1. Usuario logueado
        if (principal != null) {
            model.addAttribute("usuarioEmail", principal.getName());
        } else {
            model.addAttribute("usuarioEmail", "Invitado");
        }

        // 2. Totales para las tarjetas
        model.addAttribute("totalUsuarios", usuarioService.totalUsuarios());
        model.addAttribute("totalFichas", fichaService.totalFichas());       // 🔑 Dato real
        model.addAttribute("totalAprendices", aprendizService.totalAprendices()); // 🔑 Dato real

        return "dashboard";
    }
}