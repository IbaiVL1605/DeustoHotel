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
) {
        public void validate() {
                if (tipo == null) throw new IllegalArgumentException("El tipo no puede ser nulo");

                switch (tipo) {
                        case INDIVIDUAL, DOBLE -> validarSimple();
                        case SUITE -> validarSuite();
                }
        }

        private void validarSimple() {
                if (id_habitacion() != null)
                        throw new IllegalArgumentException("No se permite especificar habitación para tipos INDIVIDUAL o DOBLE");
                if (cantidad() == null)
                        throw new IllegalArgumentException("Se requiere especificar cantidad para tipos INDIVIDUAL o DOBLE");
        }

        private void validarSuite() {
                if (id_habitacion() == null)
                        throw new IllegalArgumentException("Se requiere especificar habitación para tipo SUITE");
                if (cantidad() != null)
                        throw new IllegalArgumentException("No se permite especificar cantidad para tipo SUITE");
        }
}
