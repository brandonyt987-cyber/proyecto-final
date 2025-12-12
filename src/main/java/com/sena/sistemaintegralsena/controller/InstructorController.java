package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.entity.Instructor;
import com.sena.sistemaintegralsena.service.InstructorService;
import com.sena.sistemaintegralsena.service.CoordinacionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/instructores")
@PreAuthorize("hasAnyRole('ADMIN', 'PSICOLOGA', 'T_SOCIAL')")
public class InstructorController {

    private final InstructorService service;
    private final CoordinacionService coordinacionService;

    public InstructorController(InstructorService service, CoordinacionService coordinacionService) {
        this.service = service;
        this.coordinacionService = coordinacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("instructores", service.listarTodos());
        return "instructores/lista";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/crear")
    public String formCrear(Model model) {
        model.addAttribute("instructor", new Instructor());
        model.addAttribute("coordinaciones", coordinacionService.listarTodas());
        return "instructores/crear";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Instructor instructor, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("coordinaciones", coordinacionService.listarTodas());
            return "instructores/crear";
        }
        try {
            service.guardar(instructor);
            redirect.addFlashAttribute("exito", "Instructor guardado correctamente.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("coordinaciones", coordinacionService.listarTodas());
            return "instructores/crear";
        }
        return "redirect:/instructores";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Instructor instructor = service.buscarPorId(id);
        if (instructor == null) return "redirect:/instructores";
        model.addAttribute("instructor", instructor);
        model.addAttribute("coordinaciones", coordinacionService.listarTodas());
        return "instructores/editar";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/actualizar")
    public String actualizar(@Valid @ModelAttribute Instructor instructor, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("coordinaciones", coordinacionService.listarTodas());
            return "instructores/editar";
        }
        try {
            service.guardar(instructor);
            redirect.addFlashAttribute("exito", "Instructor actualizado.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("coordinaciones", coordinacionService.listarTodas());
            return "instructores/editar";
        }
        return "redirect:/instructores";
    }

    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cambiar-estado/{id}")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirect) {
        service.cambiarEstado(id);
        redirect.addFlashAttribute("exito", "Estado del instructor actualizado.");
        return "redirect:/instructores";
    }
}