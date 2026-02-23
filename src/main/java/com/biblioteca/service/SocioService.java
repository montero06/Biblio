package com.biblioteca.service;

import com.biblioteca.entity.Socio;
import com.biblioteca.exception.BibliotecaException;
import com.biblioteca.repository.PrestamoRepository;
import com.biblioteca.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SocioService {

    private final SocioRepository socioRepository;
    private final PrestamoRepository prestamoRepository;

    public List<Socio> listarTodos() {
        return socioRepository.findAll();
    }

    public List<Socio> buscarPorNombre(String nombre) {
        return socioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Socio buscarPorId(Long id) {
        return socioRepository.findById(id)
            .orElseThrow(() -> BibliotecaException.notFound("Socio no encontrado con id: " + id));
    }

    public Socio crear(Socio socio) {
        if (socioRepository.existsByEmail(socio.getEmail())) {
            throw BibliotecaException.conflict("Ya existe un socio con el email: " + socio.getEmail());
        }
        return socioRepository.save(socio);
    }

    public Socio actualizar(Long id, Socio datos) {
        Socio socio = buscarPorId(id);
        if (!socio.getEmail().equals(datos.getEmail()) && socioRepository.existsByEmail(datos.getEmail())) {
            throw BibliotecaException.conflict("Ya existe un socio con el email: " + datos.getEmail());
        }
        socio.setNombre(datos.getNombre());
        socio.setEmail(datos.getEmail());
        socio.setFechaNacimiento(datos.getFechaNacimiento());
        return socioRepository.save(socio);
    }

    public void eliminar(Long id) {
        Socio socio = buscarPorId(id);
        long prestamosActivos = prestamoRepository.countBySocioAndActivoTrue(socio);
        if (prestamosActivos > 0) {
            throw BibliotecaException.conflict("No se puede eliminar un socio con préstamos activos");
        }
        socioRepository.delete(socio);
    }
}
