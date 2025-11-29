package com.sena.sistemaintegralsena.controller;
import com.sena.sistemaintegralsena.entity.Taller;
import com.sena.sistemaintegralsena.entity.Usuario;
import com.sena.sistemaintegralsena.repository.UsuarioRepository;
import com.sena.sistemaintegralsena.service.FichaService;
import com.sena.sistemaintegralsena.service.TallerService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/talleres")
@PreAuthorize("hasRole('ADMIN') or hasAnyRole('PSICOLOGA', 'T_SOCIAL')")
public class TallerController {

    private final TallerService tallerService;
    private final FichaService fichaService;
    private final UsuarioRepository usuarioRepository;

    public TallerController(TallerService tallerService, FichaService fichaService, UsuarioRepository usuarioRepository) {
        this.tallerService = tallerService;
        this.fichaService = fichaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("talleres", tallerService.listarTodos());
        return "talleres/lista";
    }

    private void cargarDatosFormulario(Model model) {
        model.addAttribute("listaFichas", fichaService.listarTodas());
        List<String> roles = List.of("PSICOLOGA", "T_SOCIAL");
        List<Usuario> profesionales = usuarioRepository.findByRolIn(roles);
        model.addAttribute("listaProfesionales", profesionales);
        model.addAttribute("minDate", LocalDate.now());
    }

    @GetMapping("/crear")
    public String formCrear(Model model) {
        model.addAttribute("taller", new Taller());
        cargarDatosFormulario(model);
        return "talleres/crear";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Taller taller, BindingResult result, RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            cargarDatosFormulario(model);
            return "talleres/crear";
        }
        try {
            tallerService.guardar(taller);
            redirect.addFlashAttribute("exito", "Taller programado correctamente.");
        } catch (Exception e) {
            cargarDatosFormulario(model);
            // Mapeo de errores específicos
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("inicio")) {
                result.rejectValue("horaInicio", "error.hora", e.getMessage());
            } else if (msg.contains("fin") || msg.contains("duración")) {
                result.rejectValue("horaFin", "error.hora", e.getMessage());
            } else {
                model.addAttribute("error", e.getMessage());
            }
            return "talleres/crear";
        }
        return "redirect:/talleres";
    }

    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Taller taller = tallerService.buscarPorId(id);
        if (taller == null) return "redirect:/talleres";
        
        model.addAttribute("taller", taller);
        cargarDatosFormulario(model);
        return "talleres/editar";
    }

    @PostMapping("/actualizar")
    public String actualizar(@Valid @ModelAttribute Taller taller, BindingResult result, RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            cargarDatosFormulario(model);
            return "talleres/editar";
        }
        try {
            tallerService.guardar(taller);
            redirect.addFlashAttribute("exito", "Taller actualizado correctamente.");
        } catch (Exception e) {
            cargarDatosFormulario(model);
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("inicio")) {
                result.rejectValue("horaInicio", "error.hora", e.getMessage());
            } else if (msg.contains("fin") || msg.contains("duración")) {
                result.rejectValue("horaFin", "error.hora", e.getMessage());
            } else {
                model.addAttribute("error", e.getMessage());
            }
            return "talleres/editar";
        }
        return "redirect:/talleres";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            tallerService.eliminar(id);
            redirect.addFlashAttribute("exito", "Taller eliminado.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se puede eliminar el taller.");
        }
        return "redirect:/talleres";
    }
}