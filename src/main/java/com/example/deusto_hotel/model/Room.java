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

    @Column(nullable = true)
    private int capacidad;

    @Column(nullable = true)
    private int precioPorNoche;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus estado = RoomStatus.DISPONIBLE;

    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL)
    private List<RoomBooking> roomBookings = new ArrayList<>();

    public void setCapacidad(int capacidad) {
        if(!this.tipo.equals(RoomType.SUITE)){
            throw new IllegalArgumentException("No se puede establecer capacidad para habitaciones que no son SUITE");
        }
        this.capacidad = capacidad;
    }

    public int getCapacidad() {
        if(this.tipo.equals(RoomType.SUITE)){
            return capacidad;
        } else {
            return tipo.getCapacidad();
        }
    }

    public int getPrecioPorNoche() {
        if(!this.tipo.equals(RoomType.SUITE)){
            return tipo.getPrecioPorNoche();
        }
        return this.precioPorNoche;
    }

    public void setPrecioPorNoche(int precioPorNoche) {
        if(!this.tipo.equals(RoomType.SUITE)){
            throw new IllegalArgumentException("No se puede establecer precio para habitaciones que no son SUITE");
        }
        
        this.precioPorNoche = precioPorNoche;
    }


}
