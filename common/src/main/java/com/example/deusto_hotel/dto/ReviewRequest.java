package com.example.deusto_hotel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
    @NotNull(message = "El ID de la habitación es obligatorio")
    Long habitacionId,

    @Min(value = 1, message = "La puntuación debe ser al menos 1")
    @Max(value = 5, message = "La puntuación debe ser como máximo 5")
    int puntuacion,

    @NotBlank(message = "El comentario es obligatorio")
    String comentario
) {}
