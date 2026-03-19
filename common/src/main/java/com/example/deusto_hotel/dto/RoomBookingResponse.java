package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.RoomBookingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RoomBookingResponse(
    Long id,
    Long clienteId,
    String clienteNombre,
    Long habitacionId,
    String habitacionNumero,
    LocalDate checkIn,
    LocalDate checkOut,
    RoomBookingStatus estado,
    Double precioTotal,
    LocalDateTime creadaEn
) {}
