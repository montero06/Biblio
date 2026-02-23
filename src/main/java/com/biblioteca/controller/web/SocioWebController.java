package com.biblioteca.controller.web;

import com.biblioteca.entity.Socio;
import com.biblioteca.exception.BibliotecaException;
import com.biblioteca.service.SocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/socios")
@RequiredArgsConstructor
public class SocioWebController {

    private final SocioService socioService;

    @GetMapping
    public String listar(@RequestParam(required = false) String busqueda, Model model) {
        if (busqueda != null && !busqueda.isBlank()) {
            model.addAttribute("socios", socioService.buscarPorNombre(busqueda));
            model.addAttribute("busqueda", busqueda);
        } else {
            model.addAttribute("socios", socioService.listarTodos());
        }
        return "socios/lista";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("socio", new Socio());
        return "socios/form";
    }

    @PostMapping("/nuevo")
    public String crear(@Valid @ModelAttribute Socio socio, BindingResult result,
                        RedirectAttributes attrs, Model model) {
        if (result.hasErrors()) return "socios/form";
        try {
            socioService.crear(socio);
            attrs.addFlashAttribute("exito", "Socio creado correctamente");
        } catch (BibliotecaException e) {
            model.addAttribute("errorNegocio", e.getMessage());
            return "socios/form";
        }
        return "redirect:/socios";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("socio", socioService.buscarPorId(id));
        return "socios/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute Socio socio,
                             BindingResult result, RedirectAttributes attrs, Model model) {
        if (result.hasErrors()) return "socios/form";
        try {
            socioService.actualizar(id, socio);
            attrs.addFlashAttribute("exito", "Socio actualizado correctamente");
        } catch (BibliotecaException e) {
            model.addAttribute("errorNegocio", e.getMessage());
            return "socios/form";
        }
        return "redirect:/socios";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes attrs) {
        try {
            socioService.eliminar(id);
            attrs.addFlashAttribute("exito", "Socio eliminado correctamente");
        } catch (BibliotecaException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/socios";
    }
}
