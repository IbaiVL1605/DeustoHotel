package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.CourtStatus;
import com.example.deusto_hotel.model.CourtType;

public record CourtResponse(
    Long id,
    String nombre,
    CourtType tipo,
    Double precioPorHora,
    CourtStatus estado
) {}
