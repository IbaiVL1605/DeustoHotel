package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.CourtType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CourtRequest(
    @NotBlank(message = "El nombre es obligatorio")
    String nombre,

    @NotNull(message = "El tipo es obligatorio")
    CourtType tipo,

    @NotNull(message = "El precio por hora es obligatorio")
    @Positive(message = "El precio por hora debe ser positivo")
    Double precioPorHora
) {}
