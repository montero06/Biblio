package com.biblioteca.controller.rest;

import com.biblioteca.entity.Socio;
import com.biblioteca.service.SocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/socios")
@RequiredArgsConstructor
public class SocioRestController {

    private final SocioService socioService;

    @GetMapping
    public List<Socio> listar(@RequestParam(required = false) String nombre) {
        if (nombre != null) return socioService.buscarPorNombre(nombre);
        return socioService.listarTodos();
    }

    @GetMapping("/{id}")
    public Socio obtener(@PathVariable Long id) {
        return socioService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Socio> crear(@Valid @RequestBody Socio socio) {
        return ResponseEntity.status(HttpStatus.CREATED).body(socioService.crear(socio));
    }

    @PutMapping("/{id}")
    public Socio actualizar(@PathVariable Long id, @Valid @RequestBody Socio socio) {
        return socioService.actualizar(id, socio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        socioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
