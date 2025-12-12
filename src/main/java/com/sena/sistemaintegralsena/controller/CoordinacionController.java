package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.entity.Coordinacion;
import com.sena.sistemaintegralsena.service.CoordinacionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/coordinaciones")
@PreAuthorize("hasAnyRole('ADMIN', 'PSICOLOGA', 'T_SOCIAL')")
public class CoordinacionController {

    private final CoordinacionService service;

    public CoordinacionController(CoordinacionService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("coordinaciones", service.listarTodas());
        return "coordinaciones/lista";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/crear")
    public String formCrear(Model model) {
        model.addAttribute("coordinacion", new Coordinacion());
        return "coordinaciones/crear";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Coordinacion coordinacion, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "coordinaciones/crear";
        }
        try {
            service.guardar(coordinacion);
            redirect.addFlashAttribute("exito", "Coordinación guardada correctamente.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "coordinaciones/crear";
        }
        return "redirect:/coordinaciones";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Coordinacion coord = service.buscarPorId(id);
        if (coord == null) return "redirect:/coordinaciones";
        model.addAttribute("coordinacion", coord);
        return "coordinaciones/editar";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/actualizar")
    public String actualizar(@Valid @ModelAttribute Coordinacion coordinacion, BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "coordinaciones/editar";
        }
        service.guardar(coordinacion);
        redirect.addFlashAttribute("exito", "Coordinación actualizada.");
        return "redirect:/coordinaciones";
    }

    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirect) {
        service.cambiarEstado(id);
        redirect.addFlashAttribute("exito", "Estado de la coordinación actualizado.");
        return "redirect:/coordinaciones";
    }
}