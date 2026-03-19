package com.example.deusto_hotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_bookings")
@Getter
@Setter
@NoArgsConstructor
public class RoomBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomBookingStatus estado = RoomBookingStatus.PENDIENTE;

    @Column(nullable = false)
    private Double precioTotal;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadaEn;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private User cliente;

    @ManyToOne
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Room habitacion;
}
