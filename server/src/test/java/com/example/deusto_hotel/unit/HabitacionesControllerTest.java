package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.Controller;
import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.proxy.Proxy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitacionesControllerTest {
    @Mock
    private Proxy proxy;

    @Mock
    private Model model;

    @InjectMocks
    private Controller controller;

    @Test
    void shouldReturnHabitacionesWithData() throws Exception {

        LocalDate entrada = LocalDate.now();
        LocalDate salida = entrada.plusDays(2);

        RoomDisponibleResponse room = mock(RoomDisponibleResponse.class);
        when(room.getTipo()).thenReturn(RoomType.INDIVIDUAL);

        ArrayList<RoomDisponibleResponse> habitaciones = new ArrayList<>();
        habitaciones.add(room);

        when(proxy.getHabitacionesDisponibles(entrada, salida))
                .thenReturn(habitaciones);

        String view = controller.getHabitacionesDisponibles(model, entrada, salida);

        assertEquals("user/habitaciones", view);

        verify(proxy).getHabitacionesDisponibles(entrada, salida);
        verify(model).addAttribute(eq("habitacionSimple"), any());
    }
    @Test
    void shouldReturnViewWithoutDates() throws Exception {

        String view = controller.getHabitacionesDisponibles(model, null, null);

        assertEquals("user/habitaciones", view);

        verify(proxy, never()).getHabitacionesDisponibles(any(), any());
    }
    @Test
    void shouldMapAllRoomTypesCorrectly() throws Exception {

        LocalDate entrada = LocalDate.now();
        LocalDate salida = entrada.plusDays(1);

        RoomDisponibleResponse room1 = mock(RoomDisponibleResponse.class);
        when(room1.getTipo()).thenReturn(RoomType.INDIVIDUAL);

        RoomDisponibleResponse room2 = mock(RoomDisponibleResponse.class);
        when(room2.getTipo()).thenReturn(RoomType.SUITE);

        RoomDisponibleResponse room3 = mock(RoomDisponibleResponse.class);
        when(room3.getTipo()).thenReturn(RoomType.DOBLE);

        ArrayList<RoomDisponibleResponse> habitaciones = new ArrayList<>();
        habitaciones.add(room1);
        habitaciones.add(room2);
        habitaciones.add(room3);

        when(proxy.getHabitacionesDisponibles(any(), any()))
                .thenReturn(habitaciones);

        controller.getHabitacionesDisponibles(model, entrada, salida);

        verify(model).addAttribute(eq("habitacionSimple"), any());
        verify(model).addAttribute(eq("habitacionSuite"), any());
        verify(model).addAttribute(eq("habitacionDoble"), any());
    }
    @Test
    void shouldHandleEmptyRoomList() throws Exception {

        LocalDate entrada = LocalDate.now();
        LocalDate salida = entrada.plusDays(1);

        when(proxy.getHabitacionesDisponibles(any(), any()))
                .thenReturn(new ArrayList<>());

        String view = controller.getHabitacionesDisponibles(model, entrada, salida);

        assertEquals("user/habitaciones", view);

        verify(proxy).getHabitacionesDisponibles(any(), any());
        verify(model, never()).addAttribute(any(), any());
    }
}