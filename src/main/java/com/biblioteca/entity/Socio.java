package com.biblioteca.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "socios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    @Column(unique = true, nullable = false)
    private String email;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    // Fecha hasta la que el socio está penalizado (null = sin penalización)
    private LocalDate fechaFinPenalizacion;

    public boolean estaPenalizado() {
        if (fechaFinPenalizacion == null) return false;
        return LocalDate.now().isBefore(fechaFinPenalizacion);
    }

    public boolean isPenalizado() {
        return estaPenalizado();
    }
}
