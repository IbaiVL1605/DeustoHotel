package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.RoomType;

public record RoomDisponiblesSimplesResponse(
        RoomType tipo,
        int numero_disponibles

) implements RoomDisponibleResponse {
    @Override
    public RoomType getTipo() {
        return this.tipo;
    }
}
