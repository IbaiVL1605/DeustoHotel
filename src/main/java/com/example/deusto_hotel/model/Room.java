package com.example.deusto_hotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType tipo;

    @Column(nullable = false)
    private int capacidad;

    @Column(nullable = false)
    private Double precioPorNoche;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus estado = RoomStatus.DISPONIBLE;

    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL)
    private List<RoomBooking> roomBookings = new ArrayList<>();
}
