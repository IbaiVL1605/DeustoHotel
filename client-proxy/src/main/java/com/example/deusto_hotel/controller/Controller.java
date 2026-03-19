package com.example.deusto_hotel.controller;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.proxy.Proxy;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.ArrayList;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class Controller {

    private final Proxy proxy;

    @GetMapping("/habitaciones")
    public String getHabitacionesDisponibles(Model model){

        ArrayList<RoomDisponibleResponse> habitaciones = proxy.getHabitacionesDisponibles();
        model.addAttribute("habitaciones", habitaciones);
        return "habitaciones";

    }
}
