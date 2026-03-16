package com.example.deusto_hotel.model;

import lombok.Getter;

@Getter
public enum RoomType {
    INDIVIDUAL(50), DOBLE(70), SUITE(200);

    private final int precio;

    RoomType(int precio) {
        this.precio = precio;
    }

}
