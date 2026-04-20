package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.RoomController;
import com.example.deusto_hotel.dto.*;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class HabitacionesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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
        LocalDate fechaEntrada = LocalDate.now().plusDays(1);
        LocalDate fechaSalida = LocalDate.now().plusDays(4);

        List<RoomDisponibleResponse> rooms = new ArrayList<>();

        rooms.add(new RoomDisponiblesSimplesResponse(RoomType.INDIVIDUAL, 5));
        rooms.add(new RoomDisponiblesSimplesResponse(RoomType.DOBLE, 2));


        List<SuitResponse> suits = new ArrayList<>();
        suits.add(new SuitResponse(4, 200, 1));
        suits.add(new SuitResponse(2, 150, 2));

        rooms.add(new RoomDisponiblesSuitResponse(RoomType.SUITE, suits));

        when(roomService.getDisponibles(fechaEntrada, fechaSalida)).thenReturn(rooms);


        mockMvc.perform(get("/api/v1/rooms/disponibles")
                        .param("fechaEntrada", fechaEntrada.toString())
                        .param("fechaSalida", fechaSalida.toString()))
                .andExpect(status().isOk()).
                andExpect(jsonPath("$[0].tipo").value("INDIVIDUAL"))
                .andExpect(jsonPath("$[0].numero_disponibles").value(5))
                .andExpect(jsonPath("$[1].tipo").value("DOBLE"))
                .andExpect(jsonPath("$[1].numero_disponibles").value(2))
                .andExpect(jsonPath("$[2].tipo").value("SUITE"))
                .andExpect(jsonPath("$[2].suites.length()").value(2));
    }

    @Test
    void createRoom_success() throws Exception {

        RoomRequest request = new RoomRequest(
                "101",
                RoomType.SUITE,
                4,
                200.0
        );

        RoomResponse response = new RoomResponse(
                1L,
                "101",
                RoomType.SUITE,
                4,
                200.0,
                null
        );

        when(roomService.create(any(RoomRequest.class))).thenReturn(response);

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/rooms")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("101"))
                .andExpect(jsonPath("$.tipo").value("SUITE"))
                .andExpect(jsonPath("$.capacidad").value(4));
    }
    @Test
    void createRoom_error_whenAlreadyExists() throws Exception {

        RoomRequest request = new RoomRequest(
                "101",
                RoomType.SUITE,
                4,
                200.0
        );

        when(roomService.create(any(RoomRequest.class)))
                .thenThrow(new IllegalArgumentException("Ya existe"));

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/rooms")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }



}