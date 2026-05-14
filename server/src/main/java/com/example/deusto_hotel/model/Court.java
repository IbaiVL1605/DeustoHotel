package com.example.deusto_hotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una pista deportiva del sistema.
 * <p>
 * Almacena la información relacionada con el tipo de pista,
 * precio por hora, estado y reservas asociadas.
 * </p>
 */
@Entity
@Table(name = "courts")
@Getter
@Setter
@NoArgsConstructor
public class Court {

    /**
     * Identificador único de la pista.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre identificativo de la pista.
     * <p>
     * Debe ser único dentro del sistema.
     * </p>
     */
    @Column(nullable = false, unique = true)
    private String nombre;

    /**
     * Tipo de pista deportiva.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourtType tipo;

    /**
     * Precio de alquiler por hora de la pista.
     */
    @Column(nullable = false)
    private Double precioPorHora;

    /**
     * Estado actual de disponibilidad de la pista.
     * <p>
     * Por defecto, las pistas se crean en estado DISPONIBLE.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourtStatus estado = CourtStatus.DISPONIBLE;

    /**
     * Lista de reservas asociadas a la pista.
     * <p>
     * Relación uno-a-muchos entre la pista y sus reservas.
     * </p>
     */
    @OneToMany(mappedBy = "pista", cascade = CascadeType.ALL)
    private List<CourtBooking> courtBookings = new ArrayList<>();
}