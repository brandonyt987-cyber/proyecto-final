package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import com.sena.sistemaintegralsena.entity.Comite;
import com.sena.sistemaintegralsena.repository.AprendizRepository;
import com.sena.sistemaintegralsena.service.ComiteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/comite")
public class ComiteController {

    private final ComiteService comiteService;
    private final AprendizRepository aprendizRepository; // Usamos el repo directo para la búsqueda rápida

    public ComiteController(ComiteService comiteService, AprendizRepository aprendizRepository) {
        this.comiteService = comiteService;
        this.aprendizRepository = aprendizRepository;
    }

    // 1. LISTAR
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("comites", comiteService.listarTodos());
        return "comite/lista";
    }

    // 2. VISTA CREAR (Buscador + Formulario)
    @GetMapping("/crear")
    public String buscarParaCrear(@RequestParam(required = false) String documento, Model model) {
        model.addAttribute("comite", new Comite());
        
        // Si el usuario ingresó un documento para buscar:
        if (documento != null && !documento.isEmpty()) {
            Aprendiz aprendiz = aprendizRepository.findByNumeroDocumento(documento);
            
            if (aprendiz != null) {
                // ¡Éxito! Encontramos al aprendiz. Lo mandamos a la vista.
                model.addAttribute("aprendizEncontrado", aprendiz);
            } else {
                // No encontrado
                model.addAttribute("errorBusqueda", "No se encontró ningún aprendiz con el documento: " + documento);
            }
            // Mantenemos el número en el input para que no lo tenga que escribir de nuevo si se equivocó
            model.addAttribute("busqueda", documento);
        }
        
        return "comite/crear";
    }

    // 3. GUARDAR
    @PostMapping("/guardar")
    public String guardar(Comite comite, 
                          @RequestParam Long aprendizId, // Recibimos el ID del aprendiz oculto en el form
                          Principal principal, // El usuario logueado
                          RedirectAttributes redirect) {
        try {
            comiteService.guardar(comite, aprendizId, principal.getName());
            redirect.addFlashAttribute("exito", "Comité registrado correctamente.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/comite/crear";
        }
        return "redirect:/comite";
    }

    // 4. ELIMINAR (Solo Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        comiteService.eliminar(id);
        redirect.addFlashAttribute("exito", "Comité eliminado.");
        return "redirect:/comite";
    }
}