package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.service.AprendizService;
import com.sena.sistemaintegralsena.service.CoordinacionService;
import com.sena.sistemaintegralsena.service.FichaService;
import com.sena.sistemaintegralsena.service.UsuarioService;
import com.sena.sistemaintegralsena.service.InstructorService;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.security.Principal;

@Controller
public class DashboardController {

    private final UsuarioService usuarioService;
    private final FichaService fichaService;
    private final AprendizService aprendizService;
    private final CoordinacionService coordinacionService;
    private final InstructorService instructorService;
    

    public DashboardController(UsuarioService usuarioService, 
                               FichaService fichaService,
                               AprendizService aprendizService,
                               CoordinacionService coordinacionService,
                               InstructorService instructorService) {
        this.usuarioService = usuarioService;
        this.fichaService = fichaService;
        this.aprendizService = aprendizService;
        this.coordinacionService = coordinacionService;
        this.instructorService = instructorService; 
        
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
        model.addAttribute("totalFichas", fichaService.totalFichas());
        model.addAttribute("totalAprendices", aprendizService.totalAprendices());
        model.addAttribute("totalCoordinaciones", coordinacionService.totalCoordinaciones());
        model.addAttribute("totalInstructores", instructorService.totalInstructores()); 
        

        return "dashboard";
    }
}