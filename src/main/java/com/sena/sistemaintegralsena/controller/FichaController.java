package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.entity.Ficha;
import com.sena.sistemaintegralsena.service.CoordinacionService; // 👈 Nuevo Import
import com.sena.sistemaintegralsena.service.FichaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/fichas")
public class FichaController {

    private final FichaService fichaService;
    private final CoordinacionService coordinacionService; // 👈 Nuevo Servicio

    public FichaController(FichaService fichaService, CoordinacionService coordinacionService) {
        this.fichaService = fichaService;
        this.coordinacionService = coordinacionService;
    }

    // 1. LISTAR
    @GetMapping
    public String listarFichas(Model model) {
        model.addAttribute("fichas", fichaService.listarTodas());
        return "fichas/lista";
    }

    // 2. CREAR VISTA
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/crear")
    public String formCrear(Model model) {
        model.addAttribute("ficha", new Ficha());
        // 👇 Enviamos las coordinaciones al select
        model.addAttribute("coordinaciones", coordinacionService.listarTodas());
        return "fichas/crear";
    }

    // 3. GUARDAR
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guardar")
    public String guardarFicha(@Valid @ModelAttribute Ficha ficha, 
                               BindingResult result, 
                               Model model, 
                               RedirectAttributes redirect) {
        if (result.hasErrors()) {
            // 👇 Si falla, recargamos la lista para que el select no quede vacío
            model.addAttribute("coordinaciones", coordinacionService.listarTodas());
            return "fichas/crear";
        }
        try {
            fichaService.guardar(ficha);
            redirect.addFlashAttribute("exito", "Ficha guardada correctamente.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("coordinaciones", coordinacionService.listarTodas());
            return "fichas/crear";
        }
        return "redirect:/fichas";
    }

    // 4. EDITAR VISTA
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Ficha ficha = fichaService.buscarPorId(id);
        if (ficha == null) return "redirect:/fichas";
        
        model.addAttribute("ficha", ficha);
        // 👇 Enviamos las coordinaciones al select
        model.addAttribute("coordinaciones", coordinacionService.listarTodas());
        return "fichas/editar";
    }

    // 5. ACTUALIZAR
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/actualizar")
    public String actualizarFicha(@Valid @ModelAttribute Ficha ficha, 
                                  BindingResult result, 
                                  Model model, 
                                  RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("coordinaciones", coordinacionService.listarTodas());
            return "fichas/editar";
        }
        try {
            fichaService.guardar(ficha);
            redirect.addFlashAttribute("exito", "Ficha actualizada correctamente.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("coordinaciones", coordinacionService.listarTodas());
            return "fichas/editar";
        }
        return "redirect:/fichas";
    }

    // 6. ELIMINAR
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminarFicha(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            fichaService.eliminar(id);
            redirect.addFlashAttribute("exito", "Ficha eliminada.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se puede eliminar la ficha porque tiene aprendices asociados.");
        }
        return "redirect:/fichas";
    }
}