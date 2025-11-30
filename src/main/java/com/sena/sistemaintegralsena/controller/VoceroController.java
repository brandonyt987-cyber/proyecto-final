package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import com.sena.sistemaintegralsena.entity.Vocero;
import com.sena.sistemaintegralsena.service.AprendizService;
import com.sena.sistemaintegralsena.service.VoceroService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/voceros")
public class VoceroController {

    private final VoceroService voceroService;
    private final AprendizService aprendizService;

    public VoceroController(VoceroService voceroService, AprendizService aprendizService) {
        this.voceroService = voceroService;
        this.aprendizService = aprendizService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("voceros", voceroService.listarTodos());
        return "voceros/lista";
    }

    private void recargarAprendizEnModelo(Model model, Vocero vocero) {
        if (vocero.getAprendiz() != null && vocero.getAprendiz().getId() != null) {
             Aprendiz ap = aprendizService.buscarPorId(vocero.getAprendiz().getId());
             model.addAttribute("aprendiz", ap);
        }
    }

    // --- CREAR (Sin cambios) ---
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/crear")
    public String formCrear(Model model) {
        model.addAttribute("vocero", new Vocero());
        model.addAttribute("busquedaRealizada", false);
        return "voceros/crear";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/buscar")
    public String buscarAprendiz(@RequestParam("documento") String documento, Model model, RedirectAttributes flash) {
        Aprendiz aprendiz = aprendizService.buscarPorDocumento(documento); 

        if (aprendiz == null) {
            flash.addFlashAttribute("error", "Aprendiz no encontrado: " + documento);
            return "redirect:/voceros/crear";
        }
        
        Vocero vocero = new Vocero();
        vocero.setAprendiz(aprendiz);

        model.addAttribute("vocero", vocero);
        model.addAttribute("aprendiz", aprendiz); 
        model.addAttribute("busquedaRealizada", true);
        
        return "voceros/crear";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Vocero vocero, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            recargarAprendizEnModelo(model, vocero);
            model.addAttribute("busquedaRealizada", true);
            return "voceros/crear";
        }
        try {
            voceroService.guardar(vocero);
            redirect.addFlashAttribute("exito", "Vocero registrado exitosamente.");
        } catch (RuntimeException e) {
            recargarAprendizEnModelo(model, vocero);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("busquedaRealizada", true);
            return "voceros/crear";
        }
        return "redirect:/voceros";
    }
    
    // --- EDITAR ---
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Vocero vocero = voceroService.buscarPorId(id);
        if (vocero == null) return "redirect:/voceros";
        
        model.addAttribute("vocero", vocero);
        model.addAttribute("aprendiz", vocero.getAprendiz()); 
        return "voceros/editar";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/buscar-reemplazo")
    public String buscarReemplazo(@RequestParam("idVocero") Long idVocero, 
                                  @RequestParam("documento") String documento, 
                                  Model model) {
        
        Vocero voceroActual = voceroService.buscarPorId(idVocero);
        Aprendiz nuevoAprendiz = aprendizService.buscarPorDocumento(documento);

        if (nuevoAprendiz == null) {
            model.addAttribute("error", "Aprendiz no encontrado: " + documento);
            model.addAttribute("aprendiz", voceroActual.getAprendiz());
        } else {
            voceroActual.setAprendiz(nuevoAprendiz);
            model.addAttribute("exitoBusqueda", "Nuevo candidato seleccionado: " + nuevoAprendiz.getNombreCompleto());
            model.addAttribute("aprendiz", nuevoAprendiz);
        }

        model.addAttribute("vocero", voceroActual);
        return "voceros/editar";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/actualizar")
    public String actualizar(@Valid @ModelAttribute Vocero vocero, BindingResult result, Model model, RedirectAttributes redirect) {
        
        // 🔑 VALIDACIÓN MANUAL: OBLIGATORIO SOLO AL EDITAR
        if (vocero.getRazonCambio() == null || vocero.getRazonCambio().trim().isEmpty()) {
            // Inyectamos el error específicamente al campo 'razonCambio'
            result.rejectValue("razonCambio", "error.user", "La razón del cambio es obligatoria para actualizar.");
        }

        if (result.hasErrors()) {
            recargarAprendizEnModelo(model, vocero);
            return "voceros/editar";
        }
        
        try {
            voceroService.guardar(vocero);
            redirect.addFlashAttribute("exito", "Vocero actualizado correctamente.");
        } catch (Exception e) {
            recargarAprendizEnModelo(model, vocero);
            model.addAttribute("error", e.getMessage()); // Error de lógica (ej: misma ficha)
            return "voceros/editar";
        }
        return "redirect:/voceros";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            voceroService.eliminar(id);
            redirect.addFlashAttribute("exito", "Vocero eliminado.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se puede eliminar.");
        }
        return "redirect:/voceros";
    }
}