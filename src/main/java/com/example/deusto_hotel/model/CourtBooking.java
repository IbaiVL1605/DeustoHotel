package com.example.deusto_hotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "court_bookings")
@Getter
@Setter
@NoArgsConstructor
public class CourtBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourtBookingStatus estado = CourtBookingStatus.PENDIENTE;

    @Column(nullable = false)
    private Double precioTotal;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private User cliente;

    @ManyToOne
    @JoinColumn(name = "pista_id", nullable = false)
    private Court pista;
}
