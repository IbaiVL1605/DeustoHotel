package com.example.deusto_hotel.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public Room(Long id, RoomType roomType) {
        this.capacidad = 1;
        this.precioPorNoche = 1;
        this.id = id;
        this.tipo = roomType;
        this.numero = String.valueOf(id);
        this.estado = RoomStatus.DISPONIBLE;

    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setPrecioPorNoche(int precioPorNoche) {
        this.precioPorNoche = precioPorNoche;
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

}
