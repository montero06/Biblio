package com.biblioteca.controller.web;

import com.biblioteca.entity.Libro;
import com.biblioteca.exception.BibliotecaException;
import com.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/libros")
@RequiredArgsConstructor
public class LibroWebController {

    private final LibroService libroService;

    @GetMapping
    public String listar(@RequestParam(required = false) String busqueda, Model model) {
        if (busqueda != null && !busqueda.isBlank()) {
            model.addAttribute("libros", libroService.buscarPorTitulo(busqueda));
            model.addAttribute("busqueda", busqueda);
        } else {
            model.addAttribute("libros", libroService.listarTodos());
        }
        return "libros/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("libro", new Libro());
        return "libros/form";
    }

    @PostMapping("/nuevo")
    public String crear(@Valid @ModelAttribute Libro libro, BindingResult result,
                        RedirectAttributes attrs, Model model) {
        if (result.hasErrors()) return "libros/form";
        try {
            libroService.crear(libro);
            attrs.addFlashAttribute("exito", "Libro creado correctamente");
        } catch (BibliotecaException e) {
            model.addAttribute("errorNegocio", e.getMessage());
            return "libros/form";
        }
        return "redirect:/libros";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("libro", libroService.buscarPorId(id));
        return "libros/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute Libro libro,
                             BindingResult result, RedirectAttributes attrs, Model model) {
        if (result.hasErrors()) return "libros/form";
        try {
            libroService.actualizar(id, libro);
            attrs.addFlashAttribute("exito", "Libro actualizado correctamente");
        } catch (BibliotecaException e) {
            model.addAttribute("errorNegocio", e.getMessage());
            return "libros/form";
        }
        return "redirect:/libros";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes attrs) {
        try {
            libroService.eliminar(id);
            attrs.addFlashAttribute("exito", "Libro eliminado correctamente");
        } catch (BibliotecaException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/libros";
    }
}
