package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.RoomController;
import com.example.deusto_hotel.dto.RoomRequest;
import com.example.deusto_hotel.dto.RoomResponse;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.service.RoomService;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class HabitacionesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RoomService roomService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getDisponibles_fecha_entrada_posterior_a_salida_error() throws Exception {
        LocalDate fechaEntrada = LocalDate.now().plusDays(5);
        LocalDate fechaSalida = LocalDate.now().plusDays(3);

        mockMvc.perform(get("/api/v1/rooms/disponibles")
                        .param("fechaEntrada", fechaEntrada.toString())
                        .param("fechaSalida", fechaSalida.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDisponibles_fecha_entrada_es_igual_salida_error() throws Exception {
        LocalDate fechaEntrada = LocalDate.now();
        LocalDate fechaSalida = LocalDate.now();

        mockMvc.perform(get("/api/v1/rooms/disponibles")
                        .param("fechaEntrada", fechaEntrada.toString())
                        .param("fechaSalida", fechaSalida.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDisponibles_fecha_entrada_es_antes_a_now_error() throws Exception {
        LocalDate fechaEntrada = LocalDate.now().minusDays(1);
        LocalDate fechaSalida = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/api/v1/rooms/disponibles")
                        .param("fechaEntrada", fechaEntrada.toString())
                        .param("fechaSalida", fechaSalida.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDisponibles_Success() throws Exception {
        LocalDate fechaEntrada = LocalDate.now().minusDays(1);
        LocalDate fechaSalida = LocalDate.now().plusDays(1);

        Room room = Room.builder()
                .id(1L)
                .tipo(RoomType.INDIVIDUAL)
                .build();
        Room room1 = Room.builder()
                .id(2L)
                .tipo(RoomType.INDIVIDUAL)
                .build();

        Room room3 = Room.builder()
                .id(3L)
                .tipo(RoomType.DOBLE)
                .build();

        Room room4 = Room.builder()
                .id(4L)
                .tipo(RoomType.SUITE)
                .build();

         RoomResponse roomResponse = new RoomResponse(room.getId(), room.getNumero(), room.getTipo(), room.getCapacidad(), room.getPrecioPorNoche(), room.getEstado());

        when(roomService.getDisponibles(fechaEntrada, fechaSalida)).thenReturn(List.of(roomResponse));

        when(roomService.getDisponibles(fechaEntrada, fechaSalida)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/rooms/disponibles")
                        .param("fechaEntrada", fechaEntrada.toString())
                        .param("fechaSalida", fechaSalida.toString()))
                .andExpect(status().isBadRequest());
    }



}