package com.biblioteca.service;

import com.biblioteca.entity.Libro;
import com.biblioteca.exception.BibliotecaException;
import com.biblioteca.repository.LibroRepository;
import com.biblioteca.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LibroService {

    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    public List<Libro> listarTodos() {
        return libroRepository.findAll();
    }

    public List<Libro> buscarPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Libro> buscarPorAutor(String autor) {
        return libroRepository.findByAutorContainingIgnoreCase(autor);
    }

    public List<Libro> listarDisponibles() {
        return libroRepository.findByDisponible(true);
    }

    public Libro buscarPorId(Long id) {
        return libroRepository.findById(id)
            .orElseThrow(() -> BibliotecaException.notFound("Libro no encontrado con id: " + id));
    }

    public Libro crear(Libro libro) {
        if (libroRepository.existsByIsbn(libro.getIsbn())) {
            throw BibliotecaException.conflict("Ya existe un libro con el ISBN: " + libro.getIsbn());
        }
        libro.setDisponible(true);
        return libroRepository.save(libro);
    }

    public Libro actualizar(Long id, Libro datos) {
        Libro libro = buscarPorId(id);
        // Si cambia el ISBN, verificar que no exista
        if (!libro.getIsbn().equals(datos.getIsbn()) && libroRepository.existsByIsbn(datos.getIsbn())) {
            throw BibliotecaException.conflict("Ya existe un libro con el ISBN: " + datos.getIsbn());
        }
        libro.setIsbn(datos.getIsbn());
        libro.setTitulo(datos.getTitulo());
        libro.setAutor(datos.getAutor());
        return libroRepository.save(libro);
    }

    public void eliminar(Long id) {
        Libro libro = buscarPorId(id);
        if (!libro.isDisponible()) {
            throw BibliotecaException.conflict("No se puede eliminar un libro que está prestado");
        }
        libroRepository.delete(libro);
    }
}
