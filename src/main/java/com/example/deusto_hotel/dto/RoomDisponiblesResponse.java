package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.RoomType;

public record RoomDisponiblesResponse(
        RoomType tipo,
        int numero_disponibles


) {
}
