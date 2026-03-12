package com.example.deusto_hotel.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    Long clienteId,
    String clienteNombre,
    Long habitacionId,
    LocalDateTime creadaEn,
    int puntuacion,
    String comentario
) {}
