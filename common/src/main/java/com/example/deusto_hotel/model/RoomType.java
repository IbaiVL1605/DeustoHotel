package com.example.deusto_hotel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RoomType {
    INDIVIDUAL(50, 1), DOBLE(70, 2), SUITE();

    private int precioPorNoche;
    private int capacidad;


}
