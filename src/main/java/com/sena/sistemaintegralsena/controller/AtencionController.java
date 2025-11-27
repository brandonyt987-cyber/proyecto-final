package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import com.sena.sistemaintegralsena.entity.Atencion;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.repository.AprendizRepository;
import com.sena.sistemaintegralsena.repository.UsuarioRepository;
import com.sena.sistemaintegralsena.service.AtencionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Comparator; 
import java.util.List;

@Controller
@RequestMapping("/atencion")
@PreAuthorize("hasRole('ADMIN') or hasAnyRole('PSICOLOGA', 'T_SOCIAL')")
public class AtencionController {

    @Autowired private AtencionService atencionService;
    @Autowired private AprendizRepository aprendizRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    // ===============================================================
    // LISTAR (MODIFICADO PARA ORDEN ASCENDENTE)
    // ===============================================================
    @GetMapping
    public String listar(Model model) {
        // 1. Obtener la lista
        List<Atencion> listaAtenciones = atencionService.listarTodas();
        
        // 2. Ordenar por ID de menor a mayor (ASC)
        listaAtenciones.sort(Comparator.comparing(Atencion::getId));
        
        // 3. Enviar a la vista
        model.addAttribute("atenciones", listaAtenciones);
        return "atencion/lista";
    }

    // MÉTODO AUXILIAR
    private void configurarVista(Model model) {
        List<String> roles = List.of("PSICOLOGA", "T_SOCIAL");
        List<Usuario> profesionales = usuarioRepository.findByRolIn(roles);
        model.addAttribute("listaProfesionales", profesionales);
        model.addAttribute("minDate", LocalDate.now());
    }
    
    private void recargarDatos(Model model, Long aprendizId) {
        Aprendiz aprendiz = aprendizRepository.findById(aprendizId).orElse(null);
        model.addAttribute("aprendizEncontrado", aprendiz);
        configurarVista(model);
    }

    // CREAR
    @GetMapping("/crear")
    public String buscarParaCrear(@RequestParam(required = false) String documento, Model model) {
        if (!model.containsAttribute("atencion")) {
            // Inicializamos con un Usuario vacío para que el path 'profesional.id' no falle en el HTML
            Atencion atencion = new Atencion();
            atencion.setProfesional(new Usuario()); 
            model.addAttribute("atencion", atencion);
        }
        
        configurarVista(model);

        if (documento != null && documento.trim().isEmpty()) {
            model.addAttribute("errorBusqueda", "Por favor, ingrese un número de documento.");
            return "atencion/crear";
        }


        if (documento != null) { 
            Aprendiz aprendiz = aprendizRepository.findByNumeroDocumento(documento);
            if (aprendiz != null) {
                model.addAttribute("aprendizEncontrado", aprendiz);
            } else {
                model.addAttribute("errorBusqueda", "No se encontró aprendiz: " + documento);
            }
            model.addAttribute("busqueda", documento);
        }
        return "atencion/crear";
    }

    // GUARDAR
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Atencion atencion, 
                          BindingResult result, 
                          @RequestParam Long aprendizId, 
                          RedirectAttributes redirect, 
                          Model model) {
        
        // 1. VALIDACIÓN MANUAL DEL PROFESIONAL
        // Validamos directamente el ID. Si es nulo, inyectamos el error en el campo específico.
        if (atencion.getProfesional() == null || atencion.getProfesional().getId() == null) {
            result.rejectValue("profesional.id", "error.profesional", "Debe seleccionar un profesional.");
        }

        // 2. Si hay errores (incluido el del profesional), volvemos al formulario
        if (result.hasErrors()) {
            recargarDatos(model, aprendizId);
            return "atencion/crear";
        }

        try {
            atencionService.guardar(atencion, aprendizId, null);
            redirect.addFlashAttribute("exito", "Atención registrada correctamente.");
        } catch (Exception e) {
            recargarDatos(model, aprendizId);
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "atencion/crear";
        }
        return "redirect:/atencion";
    }

    // EDITAR
    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Atencion atencion = atencionService.buscarPorId(id);
        if (atencion == null) return "redirect:/atencion";

        model.addAttribute("atencion", atencion);
        model.addAttribute("aprendizEncontrado", atencion.getAprendiz());
        configurarVista(model); 
        return "atencion/editar";
    }

    // ACTUALIZAR
    @PostMapping("/actualizar")
    public String actualizar(@Valid @ModelAttribute Atencion atencion, 
                             BindingResult result, 
                             @RequestParam Long aprendizId, 
                             RedirectAttributes redirect, 
                             Model model) {
        
        if (atencion.getProfesional() == null || atencion.getProfesional().getId() == null) {
            result.rejectValue("profesional.id", "error.profesional", "Debe seleccionar un profesional.");
        }

        if (result.hasErrors()) {
            recargarDatos(model, aprendizId);
            return "atencion/editar";
        }

        try {
            atencionService.guardar(atencion, aprendizId, null);
            redirect.addFlashAttribute("exito", "Atención actualizada correctamente.");
        } catch (Exception e) {
            recargarDatos(model, aprendizId);
            model.addAttribute("error", "Error: " + e.getMessage());
            return "atencion/editar";
        }
        return "redirect:/atencion";
    }

    // ELIMINAR
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        atencionService.eliminar(id);
        redirect.addFlashAttribute("exito", "Registro eliminado.");
        return "redirect:/atencion";
    }
}