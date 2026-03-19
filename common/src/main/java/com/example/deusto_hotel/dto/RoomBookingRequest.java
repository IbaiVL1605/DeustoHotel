package com.example.deusto_hotel.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RoomBookingRequest(
    @NotNull(message = "El ID de la habitación es obligatorio")
    Long habitacionId,

    @NotNull(message = "La fecha de check-in es obligatoria")
    LocalDate checkIn,

    @NotNull(message = "La fecha de check-out es obligatoria")
    LocalDate checkOut
) {}
