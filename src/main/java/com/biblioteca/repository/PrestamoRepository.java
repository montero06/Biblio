package com.biblioteca.repository;

import com.biblioteca.entity.Prestamo;
import com.biblioteca.entity.Socio;
import com.biblioteca.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findBySocio(Socio socio);

    List<Prestamo> findBySocioAndActivoTrue(Socio socio);

    long countBySocioAndActivoTrue(Socio socio);

    List<Prestamo> findByActivoTrue();

    List<Prestamo> findByLibro(Libro libro);

    @Query("SELECT p FROM Prestamo p WHERE p.activo = true AND p.fechaLimite < CURRENT_DATE")
    List<Prestamo> findPrestamosVencidos();
}
