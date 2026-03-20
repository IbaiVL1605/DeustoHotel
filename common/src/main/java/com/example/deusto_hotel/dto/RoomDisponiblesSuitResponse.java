package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.RoomType;

import java.util.List;

public record RoomDisponiblesSuitResponse(
        RoomType tipo,
        List<SuitResponse> suites
) implements RoomDisponibleResponse {
    @Override
    public RoomType getTipo() {
        return this.tipo;
    }
    public List<SuitResponse> getSuites() {
        return this.suites;
    }
}
