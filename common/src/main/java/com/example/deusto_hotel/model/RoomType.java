package com.example.deusto_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

public enum RoomType {
    INDIVIDUAL(50, 1),
    DOBLE(70, 2),
    SUITE(0, 0);

    private int precioPorNoche;
    private int capacidad;

    RoomType(int precioPorNoche, int capacidad) {
        this.precioPorNoche = precioPorNoche;
        this.capacidad = capacidad;
    }


}
