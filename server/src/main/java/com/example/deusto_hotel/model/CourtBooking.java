package com.example.deusto_hotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidad que representa una reserva de pista deportiva.
 * <p>
 * Almacena la información relacionada con la fecha,
 * horario, estado, cliente asociado y pista reservada.
 * </p>
 */
@Entity
@Table(name = "court_bookings")
@Getter
@Setter
@NoArgsConstructor
public class CourtBooking {

    /**
     * Identificador único de la reserva.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha en la que se realizará la reserva.
     */
    @Column(nullable = false)
    private LocalDate fecha;

    /**
     * Hora de inicio de la reserva.
     */
    @Column(nullable = false)
    private LocalTime horaInicio;

    /**
     * Hora de finalización de la reserva.
     */
    @Column(nullable = false)
    private LocalTime horaFin;

    /**
     * Estado actual de la reserva.
     * <p>
     * Por defecto, las reservas se crean en estado PENDIENTE.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourtBookingStatus estado = CourtBookingStatus.PENDIENTE;

    /**
     * Precio total calculado para la reserva.
     */
    @Column(nullable = false)
    private Double precioTotal;

    /**
     * Fecha y hora de creación de la reserva.
     * <p>
     * Se genera automáticamente al persistir la entidad.
     * </p>
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    /**
     * Cliente asociado a la reserva.
     */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private User cliente;

    /**
     * Pista deportiva asociada a la reserva.
     */
    @ManyToOne
    @JoinColumn(name = "pista_id", nullable = false)
    private Court pista;
}