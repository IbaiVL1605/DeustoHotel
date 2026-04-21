package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.RoomBookingController;
import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.model.RoomBookingStatus;
import com.example.deusto_hotel.service.RoomBookingService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomBookingController.class)
class RoomBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomBookingService roomBookingService;

    private ObjectMapper objectMapper = new ObjectMapper();

    //Esto es como un mini constructor por defecto para no tener que copiar
    //todos los atributos cada vez que se incializa el RoomBookingResponse
    private RoomBookingResponse buildResponse(Long id) {
        return new RoomBookingResponse(
                id,
                1L,
                "Juan",
                1L,
                "101",
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                RoomBookingStatus.CONFIRMADA,
                200.0,
                LocalDateTime.now()
        );
    }

    // =========================
    // GET ALL
    // =========================
    @Test
    void getAll_success() throws Exception {

        List<RoomBookingResponse> list = List.of(
                buildResponse(1L),
                buildResponse(2L)
        );

        when(roomBookingService.findAll()).thenReturn(list);

        mockMvc.perform(get("/api/v1/room-bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    // =========================
    // GET BY ID
    // =========================
    @Test
    void getById_success() throws Exception {

        RoomBookingResponse response = buildResponse(1L);

        when(roomBookingService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/room-bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.clienteNombre").value("Juan"))
                .andExpect(jsonPath("$.habitacionNumero").value("101"));
    }

    @Test
    void getById_notFound() throws Exception {

        when(roomBookingService.findById(1L))
                .thenThrow(new RuntimeException("Reserva no encontrada"));

        mockMvc.perform(get("/api/v1/room-bookings/1"))
                .andExpect(status().isInternalServerError());
    }

    // =========================
    // CREATE
    // =========================
    @Test
    void create_success() throws Exception {

        RoomBookingRequest request = new RoomBookingRequest(
                1L,                                      // habitacionId
                LocalDate.now().plusDays(1),              // checkIn
                LocalDate.now().plusDays(3),              // checkOut
                1L                                       // clienteId
        );

        RoomBookingResponse response = buildResponse(1L);

        when(roomBookingService.create(any(RoomBookingRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/room-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.precioTotal").value(200.0))
                .andExpect(jsonPath("$.estado").value("CONFIRMED"));
    }

    @Test
    void create_error() throws Exception {

        RoomBookingRequest request = new RoomBookingRequest(
                1L,                                      // habitacionId
                LocalDate.now().plusDays(1),              // checkIn
                LocalDate.now().plusDays(3),              // checkOut
                1L                                       // clienteId
        );

        when(roomBookingService.create(any()))
                .thenThrow(new RuntimeException());

        mockMvc.perform(post("/api/v1/room-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    // =========================
    // UPDATE
    // =========================
    @Test
    void update_success() throws Exception {

        RoomBookingRequest request = new RoomBookingRequest(
                1L,                                      // habitacionId
                LocalDate.now().plusDays(1),              // checkIn
                LocalDate.now().plusDays(3),              // checkOut
                1L                                       // clienteId
        );

        RoomBookingResponse response = buildResponse(1L);

        when(roomBookingService.update(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/room-bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precioTotal").value(200.0));
    }

    // =========================
    // DELETE
    // =========================
    @Test
    void delete_success() throws Exception {

        doNothing().when(roomBookingService).delete(1L);

        mockMvc.perform(delete("/api/v1/room-bookings/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound() throws Exception {

        doThrow(new RuntimeException())
                .when(roomBookingService).delete(1L);

        mockMvc.perform(delete("/api/v1/room-bookings/1"))
                .andExpect(status().isInternalServerError());
    }

    // =========================
    // FIND BY CLIENTE
    // =========================
    @Test
    void getByClienteId_success() throws Exception {

        List<RoomBookingResponse> list = List.of(buildResponse(1L));

        when(roomBookingService.findByClienteId(1L)).thenReturn(list);

        mockMvc.perform(get("/api/v1/room-bookings/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(1L))
                .andExpect(jsonPath("$[0].clienteNombre").value("Juan"));
    }

    // =========================
    // FIND BY HABITACION
    // =========================
    @Test
    void getByHabitacionId_success() throws Exception {

        List<RoomBookingResponse> list = List.of(buildResponse(1L));

        when(roomBookingService.findByHabitacionId(1L)).thenReturn(list);

        mockMvc.perform(get("/api/v1/room-bookings/habitacion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].habitacionId").value(1L))
                .andExpect(jsonPath("$[0].habitacionNumero").value("101"));
    }
}