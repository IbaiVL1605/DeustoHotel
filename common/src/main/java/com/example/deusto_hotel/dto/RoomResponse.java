package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.RoomStatus;
import com.example.deusto_hotel.model.RoomType;

public record RoomResponse(
    Long id,
    String numero,
    RoomType tipo,
    int capacidad,
    Double precioPorNoche,
    RoomStatus estado
) {}
