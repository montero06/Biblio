package com.biblioteca.service;

import com.biblioteca.entity.*;
import com.biblioteca.exception.BibliotecaException;
import com.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PrestamoService {

    private static final int MAX_PRESTAMOS_ACTIVOS = 3;
    private static final int DIAS_DURACION_PRESTAMO = 2;
    private static final int DIAS_PENALIZACION_POR_DIA_RETRASO = 2;

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final SocioRepository socioRepository;

    public List<Prestamo> listarTodos() {
        return prestamoRepository.findAll();
    }

    public List<Prestamo> listarActivos() {
        return prestamoRepository.findByActivoTrue();
    }

    public List<Prestamo> listarPorSocio(Long socioId) {
        Socio socio = socioRepository.findById(socioId)
            .orElseThrow(() -> BibliotecaException.notFound("Socio no encontrado"));
        return prestamoRepository.findBySocio(socio);
    }

    public List<Prestamo> listarVencidos() {
        return prestamoRepository.findPrestamosVencidos();
    }

    public Prestamo buscarPorId(Long id) {
        return prestamoRepository.findById(id)
            .orElseThrow(() -> BibliotecaException.notFound("Préstamo no encontrado con id: " + id));
    }

    /**
     * Crea un nuevo préstamo aplicando las reglas de negocio:
     * - Máximo 3 préstamos activos por socio
     * - El socio no puede estar penalizado
     * - El libro debe estar disponible
     */
    public Prestamo crear(Long libroId, Long socioId) {
        Libro libro = libroRepository.findById(libroId)
            .orElseThrow(() -> BibliotecaException.notFound("Libro no encontrado con id: " + libroId));

        Socio socio = socioRepository.findById(socioId)
            .orElseThrow(() -> BibliotecaException.notFound("Socio no encontrado con id: " + socioId));

        // Regla: libro disponible
        if (!libro.isDisponible()) {
            throw BibliotecaException.conflict("El libro '" + libro.getTitulo() + "' no está disponible");
        }

        // Regla: socio no penalizado
        if (socio.estaPenalizado()) {
            throw BibliotecaException.conflict(
                "El socio está penalizado hasta el " + socio.getFechaFinPenalizacion() +
                ". No puede realizar nuevos préstamos.");
        }

        // Regla: máximo 3 préstamos activos
        long prestamosActivos = prestamoRepository.countBySocioAndActivoTrue(socio);
        if (prestamosActivos >= MAX_PRESTAMOS_ACTIVOS) {
            throw BibliotecaException.conflict(
                "El socio ya tiene " + MAX_PRESTAMOS_ACTIVOS + " préstamos activos. No puede solicitar más.");
        }

        // Crear préstamo
        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setSocio(socio);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaLimite(LocalDate.now().plusDays(DIAS_DURACION_PRESTAMO));
        prestamo.setActivo(true);

        // Marcar libro como no disponible
        libro.setDisponible(false);
        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);
    }

    /**
     * Devuelve un préstamo aplicando penalizaciones si hay retraso:
     * - Por cada día de retraso, el socio queda penalizado 2 días
     */
    public Prestamo devolver(Long prestamoId) {
        Prestamo prestamo = buscarPorId(prestamoId);

        if (!prestamo.isActivo()) {
            throw BibliotecaException.conflict("El préstamo ya fue devuelto");
        }

        LocalDate hoy = LocalDate.now();
        prestamo.setFechaDevolucion(hoy);
        prestamo.setActivo(false);

        // Calcular retraso y penalización
        long diasRetraso = prestamo.calcularDiasRetraso();
        if (diasRetraso > 0) {
            long diasPenalizacion = diasRetraso * DIAS_PENALIZACION_POR_DIA_RETRASO;
            Socio socio = prestamo.getSocio();

            // Si ya tiene penalización activa, se extiende desde esa fecha
            LocalDate basePenalizacion = (socio.estaPenalizado())
                ? socio.getFechaFinPenalizacion()
                : hoy;

            socio.setFechaFinPenalizacion(basePenalizacion.plusDays(diasPenalizacion));
            socioRepository.save(socio);
        }

        // Marcar libro como disponible
        Libro libro = prestamo.getLibro();
        libro.setDisponible(true);
        libroRepository.save(libro);

        return prestamoRepository.save(prestamo);
    }

    public void eliminar(Long id) {
        Prestamo prestamo = buscarPorId(id);
        if (prestamo.isActivo()) {
            throw BibliotecaException.conflict("No se puede eliminar un préstamo activo. Devuélvalo primero.");
        }
        prestamoRepository.delete(prestamo);
    }
}
