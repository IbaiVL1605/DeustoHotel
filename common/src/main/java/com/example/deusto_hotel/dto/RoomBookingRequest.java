package com.example.deusto_hotel.dto;

import com.example.deusto_hotel.model.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.ToString;

import java.time.LocalDate;

public record RoomBookingRequest(
        @NotNull
        RoomType tipo,
        @NotNull 
        @Positive Long id_cliente,
        @Positive Integer cantidad,
        @Positive Long id_habitacion,
        @NotNull LocalDate fechaEntrada,
        @NotNull LocalDate fechaSalida
) {}
