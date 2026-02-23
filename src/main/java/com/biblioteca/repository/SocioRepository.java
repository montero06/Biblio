package com.biblioteca.repository;

import com.biblioteca.entity.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SocioRepository extends JpaRepository<Socio, Long> {
    List<Socio> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByEmail(String email);
}
