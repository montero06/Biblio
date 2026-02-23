package com.biblioteca;

import com.biblioteca.entity.*;
import com.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final LibroRepository libroRepository;
    private final SocioRepository socioRepository;

    @Override
    public void run(String... args) {
        if (libroRepository.count() == 0) {
            libroRepository.save(new Libro(null, "978-84-01-02222-1", "El Quijote", "Miguel de Cervantes", true));
            libroRepository.save(new Libro(null, "978-84-01-02223-2", "Cien años de soledad", "Gabriel García Márquez", true));
            libroRepository.save(new Libro(null, "978-84-01-02224-3", "La sombra del viento", "Carlos Ruiz Zafón", true));
            libroRepository.save(new Libro(null, "978-84-01-02225-4", "1984", "George Orwell", true));
            libroRepository.save(new Libro(null, "978-84-01-02226-5", "El señor de los anillos", "J.R.R. Tolkien", true));
        }

        if (socioRepository.count() == 0) {
            socioRepository.save(new Socio(null, "Ana García López", "ana.garcia@email.com", LocalDate.of(1990, 5, 15), null));
            socioRepository.save(new Socio(null, "Carlos Martínez Ruiz", "carlos.martinez@email.com", LocalDate.of(1985, 8, 22), null));
            socioRepository.save(new Socio(null, "María López Sánchez", "maria.lopez@email.com", LocalDate.of(1995, 3, 10), null));
        }
    }
}
