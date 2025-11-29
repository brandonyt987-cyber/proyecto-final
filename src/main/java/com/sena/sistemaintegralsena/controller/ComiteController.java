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
import java.util.Comparator;
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

    // ===========================================================
    // MÉTODO MODIFICADO PARA ORDEN ASCENDENTE (ID 1, 2, 3...)
    // ===========================================================
    @GetMapping
    public String listar(Model model) {
        // 1. Obtenemos la lista del servicio
        List<Comite> listaComites = comiteService.listarTodos();
        
        // 2. La ordenamos por ID de forma ASCENDENTE (del más viejo al más nuevo)
        listaComites.sort(Comparator.comparing(Comite::getId));
        
        // 3. Enviamos la lista ya ordenada a la vista
        model.addAttribute("comites", listaComites);
        
        return "comite/lista";
    }

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

    private void repararModeloError(Model model, Long aprendizId) {
        if (aprendizId != null) {
            Aprendiz aprendiz = aprendizRepository.findById(aprendizId).orElse(null);
            model.addAttribute("aprendizEncontrado", aprendiz); 
        }
        cargarListas(model);
        configurarLimitesFecha(model);
    }

    @GetMapping("/crear")
    public String buscarParaCrear(@RequestParam(required = false) String documento, Model model) {
        Comite comite = new Comite();
        if (model.containsAttribute("comite")) {
            comite = (Comite) model.getAttribute("comite");
        } else {
            model.addAttribute("comite", comite);
        }
        
        configurarLimitesFecha(model); 

        if (documento != null && documento.trim().isEmpty()) {
    model.addAttribute("errorBusqueda", "Por favor, ingrese un número de documento.");
    return "comite/crear";
}

        if (documento != null && !documento.isEmpty()) {
            Aprendiz aprendiz = aprendizRepository.findByNumeroDocumento(documento);
            
            if (aprendiz != null) {
                
                if (voceroRepository.existsByAprendizId(aprendiz.getId())) {
                    model.addAttribute("errorBusqueda", "EL APRENDIZ ES VOCERO. Debe realizar el cambio de vocería antes de asignarle un comité.");
                    model.addAttribute("busqueda", documento);
                    return "comite/crear";
                }

                if (aprendiz.getFicha() != null) {
                    Vocero voceroFicha = voceroRepository.findByAprendizFichaId(aprendiz.getFicha().getId());
                    Comite comiteForm = (Comite) model.getAttribute("comite");
                    if (voceroFicha == null) {
                        model.addAttribute("errorBusqueda", "LA FICHA " + aprendiz.getFicha().getCodigo() + " NO TIENE VOCERO ASIGNADO. Asigne uno primero.");
                        model.addAttribute("busqueda", documento);
                        return "comite/crear"; 
                    }
                    comiteForm.setRepresentanteAprendices(voceroFicha.getAprendiz().getNombreCompleto());
                }

                model.addAttribute("aprendizEncontrado", aprendiz);
                cargarListas(model);
            } else {
                model.addAttribute("errorBusqueda", "No se encontró aprendiz: " + documento);
            }
            model.addAttribute("busqueda", documento);
        }
        return "comite/crear";
    }

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
            model.addAttribute("error", "Error al guardar: " + e.getMessage());
            return "comite/crear";
        }
        return "redirect:/comite";
    }

    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Comite comite = comiteService.buscarPorId(id);
        if (comite == null) return "redirect:/comite";

        model.addAttribute("comite", comite);
        repararModeloError(model, comite.getAprendiz().getId());

        return "comite/editar";
    }

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
            model.addAttribute("error", "Error: " + e.getMessage());
            return "comite/editar";
        }
        return "redirect:/comite";
    }

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