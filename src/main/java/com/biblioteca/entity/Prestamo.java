package com.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "prestamos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "libro_id")
    private Libro libro;

    @ManyToOne(optional = false)
    @JoinColumn(name = "socio_id")
    private Socio socio;

    @Column(nullable = false)
    private LocalDate fechaPrestamo;

    @Column(nullable = false)
    private LocalDate fechaLimite;

    private LocalDate fechaDevolucion;

    @Column(nullable = false)
    private boolean activo = true;

    // Días de retraso (0 si se devolvió a tiempo o aún no devuelto)
    public long calcularDiasRetraso() {
        LocalDate referencia = (fechaDevolucion != null) ? fechaDevolucion : LocalDate.now();
        if (referencia.isAfter(fechaLimite)) {
            return fechaLimite.until(referencia).getDays();
        }
        return 0;
    }
}
