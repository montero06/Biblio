package com.biblioteca.controller.web;

import com.biblioteca.exception.BibliotecaException;
import com.biblioteca.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/prestamos")
@RequiredArgsConstructor
public class PrestamoWebController {

    private final PrestamoService prestamoService;
    private final LibroService libroService;
    private final SocioService socioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("prestamos", prestamoService.listarTodos());
        return "prestamos/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("libros", libroService.listarDisponibles());
        model.addAttribute("socios", socioService.listarTodos());
        return "prestamos/form";
    }

    @PostMapping("/nuevo")
    public String crear(@RequestParam Long libroId, @RequestParam Long socioId,
                        RedirectAttributes attrs, Model model) {
        try {
            prestamoService.crear(libroId, socioId);
            attrs.addFlashAttribute("exito", "Préstamo creado correctamente");
        } catch (BibliotecaException e) {
            model.addAttribute("errorNegocio", e.getMessage());
            model.addAttribute("libros", libroService.listarDisponibles());
            model.addAttribute("socios", socioService.listarTodos());
            return "prestamos/form";
        }
        return "redirect:/prestamos";
    }

    @PostMapping("/{id}/devolver")
    public String devolver(@PathVariable Long id, RedirectAttributes attrs) {
        try {
            prestamoService.devolver(id);
            attrs.addFlashAttribute("exito", "Libro devuelto correctamente");
        } catch (BibliotecaException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes attrs) {
        try {
            prestamoService.eliminar(id);
            attrs.addFlashAttribute("exito", "Préstamo eliminado correctamente");
        } catch (BibliotecaException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/prestamos";
    }
}
