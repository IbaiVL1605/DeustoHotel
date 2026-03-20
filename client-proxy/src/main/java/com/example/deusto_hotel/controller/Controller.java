package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.proxy.Proxy;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class Controller {

    private final Proxy proxy;

    @GetMapping("/habitaciones/disponibles")
    public String getHabitacionesDisponibles(Model model, LocalDate fechaEntrada, LocalDate fechaSalida) throws IOException, InterruptedException {

        if (fechaEntrada == null || fechaSalida == null) {
            return "user/habitaciones";
        }

        ArrayList<RoomDisponibleResponse> habitaciones = proxy.getHabitacionesDisponibles(fechaEntrada, fechaSalida);

        System.out.printf(habitaciones.toString());

        habitaciones.forEach(habitacion -> {
            String key = switch (habitacion.getTipo()) {
                case INDIVIDUAL -> "habitacionSimple";
                case DOBLE  -> "habitacionDoble";
                case SUITE  -> "habitacionSuite";
            };
            model.addAttribute(key, habitacion);
        });
        return "user/habitaciones";

    }



}