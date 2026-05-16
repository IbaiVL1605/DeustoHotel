package com.example.deusto_hotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa una reserva de habitación en el hotel.
 * <p>
 * Esta clase modela las reservas de habitaciones del sistema de gestión hotelera,
 * permitiendo almacenar información sobre las fechas de entrada y salida, el estado
 * de la reserva, el precio total y los detalles del cliente y la habitación asociados.
 * </p>
 * <p>
 * Cada reserva tiene un estado que puede ser PENDIENTE, CONFIRMADA, CANCELADA u otro
 * estado definido en {@link RoomBookingStatus}. Las reservas se crean con estado
 * PENDIENTE por defecto.
 * </p>
 *
 * @author Deusto Hotel Team
 * @version 1.0
 * @see Room
 * @see User
 * @see RoomBookingStatus
 */
@Entity
@Table(name = "room_bookings")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class RoomBooking {

    /** Identificador único de la reserva de habitación. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Fecha de entrada (check-in) de la reserva. */
    @Column(nullable = false)
    private LocalDate checkIn;

    /** Fecha de salida (check-out) de la reserva. */
    @Column(nullable = false)
    private LocalDate checkOut;

    /** Estado actual de la reserva (PENDIENTE, CONFIRMADA, CANCELADA, etc.). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomBookingStatus estado = RoomBookingStatus.PENDIENTE;

    /** Precio total de la reserva en unidades monetarias. */
    @Column(nullable = false)
    private Integer precioTotal;

    /** Fecha y hora de creación de la reserva. Se establece automáticamente al insertar el registro. */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    /** Cliente que realizó la reserva. Relación muchos-a-uno con la entidad User. */
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private User cliente;

    /** Habitación reservada. Relación muchos-a-uno con la entidad Room. */
    @ManyToOne
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Room habitacion;
}
