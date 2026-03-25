package com.example.deusto_hotel.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RoomBookingRequest(
        @NotNull Long habitacionId,
        @NotNull LocalDate checkIn,
        @NotNull LocalDate checkOut,
        @NotNull Long clienteId
) {}
