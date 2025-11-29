package com.sena.sistemaintegralsena.controller;

import com.sena.sistemaintegralsena.entity.Aprendiz;
import com.sena.sistemaintegralsena.service.AprendizExcelService;
import com.sena.sistemaintegralsena.service.AprendizService;
import com.sena.sistemaintegralsena.service.FichaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/aprendices")
public class AprendizController {

    private final AprendizService aprendizService;
    private final FichaService fichaService;
    private final AprendizExcelService excelService;

    public AprendizController(AprendizService aprendizService, 
                              FichaService fichaService,
                              AprendizExcelService excelService) {
        this.aprendizService = aprendizService;
        this.fichaService = fichaService;
        this.excelService = excelService;
    }

    // MÉTODO AUXILIAR PARA LA EDAD
    private void configurarLimiteEdad(Model model) {
        // La fecha máxima de nacimiento permitida es HOY menos 16 AÑOS
        // Ejemplo: Si hoy es 2025, la fecha max es 2009. Alguien nacido en 2010 tendría 15.
        LocalDate maxDate = LocalDate.now().minusYears(16);
        model.addAttribute("maxDate", maxDate);
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("aprendices", aprendizService.listarTodos());
        return "aprendices/lista";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/crear")
    public String formCrear(Model model) {
        model.addAttribute("aprendiz", new Aprendiz());
        model.addAttribute("fichas", fichaService.listarTodas());
        configurarLimiteEdad(model); 
        return "aprendices/crear";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Aprendiz aprendiz, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("fichas", fichaService.listarTodas());
            configurarLimiteEdad(model); 
            return "aprendices/crear";
        }
        try {
            aprendizService.guardar(aprendiz);
            redirect.addFlashAttribute("exito", "Aprendiz registrado correctamente.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("fichas", fichaService.listarTodas());
            configurarLimiteEdad(model); 
            return "aprendices/crear";
        }
        return "redirect:/aprendices";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        Aprendiz aprendiz = aprendizService.buscarPorId(id);
        if (aprendiz == null) return "redirect:/aprendices";
        model.addAttribute("aprendiz", aprendiz);
        model.addAttribute("fichas", fichaService.listarTodas());
        configurarLimiteEdad(model); 
        return "aprendices/editar";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/actualizar")
    public String actualizar(@Valid @ModelAttribute Aprendiz aprendiz, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("fichas", fichaService.listarTodas());
            configurarLimiteEdad(model);
            return "aprendices/editar";
        }
        try {
            aprendizService.guardar(aprendiz);
            redirect.addFlashAttribute("exito", "Aprendiz actualizado correctamente.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("fichas", fichaService.listarTodas());
            configurarLimiteEdad(model);
            return "aprendices/editar";
        }
        return "redirect:/aprendices";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            aprendizService.eliminar(id);
            redirect.addFlashAttribute("exito", "Aprendiz eliminado.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "No se puede eliminar.");
        }
        return "redirect:/aprendices";
    }

    // Métodos de importación
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/importar")
    public String vistaImportar() {
        return "aprendices/importar";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/upload")
    public String subirExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirect) {
        if (file.isEmpty()) {
            redirect.addFlashAttribute("error", "Por favor seleccione un archivo.");
            return "redirect:/aprendices/importar";
        }
        try {
            excelService.guardar(file);
            redirect.addFlashAttribute("exito", "Carga masiva exitosa.");
            return "redirect:/aprendices";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error al cargar: " + e.getMessage());
            return "redirect:/aprendices/importar";
        }
    }
}