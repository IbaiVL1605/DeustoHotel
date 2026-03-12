package com.example.deusto_hotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courts")
@Getter
@Setter
@NoArgsConstructor
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourtType tipo;

    @Column(nullable = false)
    private Double precioPorHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourtStatus estado = CourtStatus.DISPONIBLE;

    @OneToMany(mappedBy = "pista", cascade = CascadeType.ALL)
    private List<CourtBooking> courtBookings = new ArrayList<>();
}
