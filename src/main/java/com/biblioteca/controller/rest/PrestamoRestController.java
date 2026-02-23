package com.biblioteca.controller.rest;

import com.biblioteca.entity.Prestamo;
import com.biblioteca.service.PrestamoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoRestController {

    private final PrestamoService prestamoService;

    @GetMapping
    public List<Prestamo> listar(@RequestParam(required = false) Boolean activos) {
        if (Boolean.TRUE.equals(activos)) return prestamoService.listarActivos();
        return prestamoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Prestamo obtener(@PathVariable Long id) {
        return prestamoService.buscarPorId(id);
    }

    @GetMapping("/vencidos")
    public List<Prestamo> vencidos() {
        return prestamoService.listarVencidos();
    }

    @PostMapping
    public ResponseEntity<Prestamo> crear(@RequestBody Map<String, Long> body) {
        Long libroId = body.get("libroId");
        Long socioId = body.get("socioId");
        Prestamo prestamo = prestamoService.crear(libroId, socioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamo);
    }

    @PatchMapping("/{id}/devolver")
    public Prestamo devolver(@PathVariable Long id) {
        return prestamoService.devolver(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        prestamoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
