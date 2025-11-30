package com.sena.sistemaintegralsena.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "voceros")
public class Vocero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Debe asignar un Aprendiz")
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aprendiz_id", unique = true, nullable = false)
    private Aprendiz aprendiz;

    // 👇 YA NO TIENE @NotBlank (La validación será manual en el Servicio)
    @Column(columnDefinition = "TEXT")
    private String razonCambio;
}