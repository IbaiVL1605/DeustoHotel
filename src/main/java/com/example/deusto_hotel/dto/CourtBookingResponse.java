package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.CourtBookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record CourtBookingResponse(
    Long id,
    Long clienteId,
    String clienteNombre,
    Long pistaId,
    String pistaNombre,
    LocalDate fecha,
    LocalTime horaInicio,
    LocalTime horaFin,
    CourtBookingStatus estado,
    Double precioTotal,
    LocalDateTime creadaEn
) {}
