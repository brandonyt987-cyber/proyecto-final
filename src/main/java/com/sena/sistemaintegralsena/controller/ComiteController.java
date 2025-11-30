package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import com.sena.sistemaintegralsena.entity.Comite;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.entity.Vocero;
import com.sena.sistemaintegralsena.repository.AprendizRepository;
import com.sena.sistemaintegralsena.repository.UsuarioRepository;
import com.sena.sistemaintegralsena.repository.VoceroRepository;
import com.sena.sistemaintegralsena.service.ComiteService;
import com.sena.sistemaintegralsena.service.CoordinacionService;
import com.sena.sistemaintegralsena.service.InstructorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/comite")
@PreAuthorize("hasRole('ADMIN') or hasAnyRole('PSICOLOGA', 'T_SOCIAL')")
public class ComiteController {

    @Autowired private ComiteService comiteService;
    @Autowired private AprendizRepository aprendizRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private InstructorService instructorService;
    @Autowired private CoordinacionService coordinacionService;
    @Autowired private VoceroRepository voceroRepository;

    // LISTAR
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("comites", comiteService.listarTodos());
        return "comite/lista";
    }

    // MÉTODO INTELIGENTE DE FECHAS
    private void configurarLimitesFecha(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate minDate, maxDate;

        if (today.getDayOfMonth() > 20) {
            minDate = today.plusMonths(1).withDayOfMonth(1);
            maxDate = today.plusMonths(1).withDayOfMonth(20);
        } else {
            minDate = today;
            maxDate = today.withDayOfMonth(20);
        }
        
        model.addAttribute("minDate", minDate);
        model.addAttribute("maxDate", maxDate);
        model.addAttribute("minDatePlazo", today);
        model.addAttribute("maxDatePlazo", today.plusMonths(1));
    }

    // MÉTODO AUXILIAR PARA RECARGAR DATOS EN CASO DE ERROR
    private void repararModeloError(Model model, Long aprendizId) {
        if (aprendizId != null) {
            Aprendiz aprendiz = aprendizRepository.findById(aprendizId).orElse(null);
            model.addAttribute("aprendizEncontrado", aprendiz); 
        }
        cargarListas(model);
        configurarLimitesFecha(model);
    }

    // VISTA CREAR
    @GetMapping("/crear")
    public String buscarParaCrear(@RequestParam(required = false) String documento, Model model) {
        Comite comite = new Comite();
        if (model.containsAttribute("comite")) {
            comite = (Comite) model.getAttribute("comite");
        } else {
            model.addAttribute("comite", comite);
        }
        
        configurarLimitesFecha(model); 

        if (documento != null && !documento.isEmpty()) {
            Aprendiz aprendiz = aprendizRepository.findByNumeroDocumento(documento);
            
            if (aprendiz != null) {
                
                // 1. VALIDACIÓN: ¿EL APRENDIZ ES VOCERO? (Bloqueante)
                if (voceroRepository.existsByAprendizId(aprendiz.getId())) {
                    model.addAttribute("errorBusqueda", "⚠️ EL APRENDIZ ES VOCERO. Debe realizar el cambio de vocería antes de asignarle un comité.");
                    model.addAttribute("busqueda", documento);
                    return "comite/crear";
                }

                // 2. VALIDACIÓN: ¿LA FICHA TIENE VOCERO? (Bloqueante)
                if (aprendiz.getFicha() != null) {
                    Vocero voceroFicha = voceroRepository.findByAprendizFichaId(aprendiz.getFicha().getId());
                    
                    if (voceroFicha == null) {
                        // 👇 AQUÍ ESTÁ LA NUEVA VALIDACIÓN 👇
                        model.addAttribute("errorBusqueda", "⚠️ LA FICHA " + aprendiz.getFicha().getCodigo() + " NO TIENE VOCERO ASIGNADO. Debe asignar un vocero a esta ficha en el módulo 'Voceros' antes de crear el comité.");
                        model.addAttribute("busqueda", documento);
                        return "comite/crear"; // Bloqueamos el proceso
                    }
                    
                    // Si existe, lo asignamos automáticamente
                    comite.setRepresentanteAprendices(voceroFicha.getAprendiz().getNombreCompleto());
                }

                // Si todo está bien, cargamos el formulario
                model.addAttribute("aprendizEncontrado", aprendiz);
                cargarListas(model);
            } else {
                model.addAttribute("errorBusqueda", "No se encontró aprendiz: " + documento);
            }
            model.addAttribute("busqueda", documento);
        }
        return "comite/crear";
    }

    // GUARDAR
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Comite comite, 
                          BindingResult result,
                          @RequestParam Long aprendizId, 
                          Principal principal,
                          RedirectAttributes redirect,
                          Model model) {
        
        if (result.hasErrors()) {
            repararModeloError(model, aprendizId);
            return "comite/crear"; 
        }

        try {
            comiteService.guardar(comite, aprendizId, principal.getName());
            redirect.addFlashAttribute("exito", "Comité registrado correctamente.");
        } catch (Exception e) {
            repararModeloError(model, aprendizId);
            if (e.getMessage().contains("Hora") || e.getMessage().contains("Horario")) {
                result.rejectValue("hora", "error.hora", e.getMessage());
            } else {
                model.addAttribute("error", e.getMessage());
            }
            return "comite/crear";
        }
        return "redirect:/comite";
    }

    // VISTA EDITAR
    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Comite comite = comiteService.buscarPorId(id);
        if (comite == null) return "redirect:/comite";

        model.addAttribute("comite", comite);
        model.addAttribute("aprendizEncontrado", comite.getAprendiz());
        cargarListas(model);
        configurarLimitesFecha(model);

        return "comite/editar";
    }

    // ACTUALIZAR
    @PostMapping("/actualizar")
    public String actualizar(@Valid @ModelAttribute Comite comite, 
                             BindingResult result,
                             @RequestParam Long aprendizId, 
                             Principal principal,
                             RedirectAttributes redirect,
                             Model model) {
        
        if (result.hasErrors()) {
            repararModeloError(model, aprendizId);
            return "comite/editar";
        }

        try {
            comiteService.guardar(comite, aprendizId, principal.getName());
            redirect.addFlashAttribute("exito", "Comité actualizado correctamente.");
        } catch (Exception e) {
            repararModeloError(model, aprendizId);
            if (e.getMessage().contains("Hora") || e.getMessage().contains("Horario")) {
                result.rejectValue("hora", "error.hora", e.getMessage());
            } else {
                model.addAttribute("error", e.getMessage());
            }
            return "comite/editar";
        }
        return "redirect:/comite";
    }

    // ELIMINAR
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        comiteService.eliminar(id);
        redirect.addFlashAttribute("exito", "Comité eliminado.");
        return "redirect:/comite";
    }

    private void cargarListas(Model model) {
        try {
            List<String> rolesPermitidos = List.of("PSICOLOGA", "T_SOCIAL");
            List<Usuario> profesionales = usuarioRepository.findByRolIn(rolesPermitidos);
            model.addAttribute("listaProfesionales", profesionales);
            model.addAttribute("listaInstructores", instructorService.listarTodos());
            model.addAttribute("listaCoordinaciones", coordinacionService.listarTodas());
        } catch (Exception e) {
            System.err.println("Error cargando listas: " + e.getMessage());
        }
    }
}