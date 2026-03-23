package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.WeekAvailability;
import com.example.deusto_hotel.proxy.Proxy;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class Controller {

    private final Proxy proxy;

    @GetMapping("/habitaciones/disponibles")
    public String getHabitacionesDisponibles(Model model, LocalDate fechaEntrada, LocalDate fechaSalida) throws IOException, InterruptedException, JsonProcessingException {

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

    @GetMapping("/pistas")
    public String getPistas(
            Model model,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) throws IOException, InterruptedException, JsonProcessingException {
        LocalDate today = LocalDate.now();
        int currentYear = (year != null) ? year : today.getYear();
        int currentMonth = (month != null) ? month : today.getMonthValue();

        List<WeekAvailability> disponibilidad = proxy.getCourtsWeeklyAvailability(currentYear, currentMonth, tipo);

        model.addAttribute("disponibilidad", disponibilidad);
        model.addAttribute("tipo", tipo);
        model.addAttribute("year", currentYear);
        model.addAttribute("month", currentMonth);
        model.addAttribute("hoy", today);
        return "user/pistas";
    }
}