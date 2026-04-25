package com.example.deusto_hotel.unit.controller;

import com.example.deusto_hotel.controller.RoomBookingController;
import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.service.RoomBookingService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Tag("unit")

@WebMvcTest(RoomBookingController.class)
class RoomBookingControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomBookingService roomBookingService;

    @Autowired
    private ObjectMapper objectMapper;


// DELETE OK

    @Test
    void delete_success() throws Exception {

        doNothing().when(roomBookingService).delete(1L, 10L);

        mockMvc.perform(delete("/api/v1/room-bookings/1")
                        .param("userId", "10"))
                .andExpect(status().isNoContent());
    }


// DELETE NOT FOUND

    @Test
    void delete_notFound() throws Exception {

        doThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Reserva no encontrada"))
                .when(roomBookingService).delete(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/room-bookings/1")
                        .param("userId", "10"))
                .andExpect(status().isBadRequest());
    }


// DELETE UNAUTHORIZED

    @Test
    void delete_unauthorized_whenNoUser() throws Exception {

        mockMvc.perform(delete("/api/v1/room-bookings/1"))
                .andExpect(status().isBadRequest());
    }

    // --- 1. TEST DE ÉXITO ---
    @Test
    void create_ShouldReturnCreated_WhenRequestIsValid() throws Exception {
        // GIVEN: Una lista con un request válido (INDIVIDUAL con cantidad)
        RoomBookingRequest validRequest = new RoomBookingRequest(
                RoomType.INDIVIDUAL, 1L, 2, null,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)
        );
        List<RoomBookingRequest> requestList = List.of(validRequest);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/room-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList)))
                .andExpect(status().isCreated());

        // Verificamos que el controlador realmente llamó al servicio
        verify(roomBookingService).create(anyList());
    }

    // --- 2. TEST DE ERROR DE NEGOCIO (Lanzado por el Record) ---
    @Test
    void create_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // GIVEN: Un request que el record considerará inválido (SUITE con cantidad)
        // El controller llamará a .validate() y saltará la excepción
        RoomBookingRequest invalidRequest = new RoomBookingRequest(
                RoomType.SUITE, 1L, 5, 101L,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)
        );
        List<RoomBookingRequest> requestList = List.of(invalidRequest);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/room-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestList)))
                .andExpect(status().isBadRequest());


    }

    // --- 3. TEST DE ERROR EN EL SERVICIO (Cliente no encontrado) ---
    @Test
    void create_ShouldReturnNotFound_WhenServiceThrowsException() throws Exception {
        // GIVEN
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.INDIVIDUAL, 99L, 1, null,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)
        );

        // Simulamos que el servicio falla (ej: cliente no existe)
        doThrow(new IllegalArgumentException("Cliente no encontrado"))
                .when(roomBookingService).create(anyList());

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/room-bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(request))))
                .andExpect(status().isBadRequest());
    }


}
