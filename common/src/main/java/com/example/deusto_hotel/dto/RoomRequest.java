package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RoomRequest(
    @NotBlank(message = "El número es obligatorio")
    String numero,

    @NotNull(message = "El tipo es obligatorio")
    RoomType tipo,

    @Positive(message = "La capacidad debe ser positiva")
    int capacidad,

    @NotNull(message = "El precio por noche es obligatorio")
    @Positive(message = "El precio por noche debe ser positivo")
    Double precioPorNoche
) {}
