package com.example.deusto_hotel.unit.controller;

import com.example.deusto_hotel.controller.RoomBookingController;
import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.model.RoomBookingStatus;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Test
    void getByClienteId_exito() throws Exception {
        RoomBookingResponse response = new RoomBookingResponse(1L, 1L, "User", 1L, "Room", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), RoomBookingStatus.PENDIENTE, 100.0, LocalDateTime.now());
        when(roomBookingService.findByClienteId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/room-bookings/cliente/1"))
                .andExpect(status().isOk());

    }

    @Test
    void getByClienteId_error() throws Exception {
        when(roomBookingService.findByClienteId(99L)).thenThrow(new IllegalArgumentException("Cliente no encontrado"));

        mockMvc.perform(get("/api/v1/room-bookings/cliente/99"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void getAll_exito() throws Exception {
        RoomBookingResponse response1 = new RoomBookingResponse(1L, 1L, "User", 1L, "Room", LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), RoomBookingStatus.PENDIENTE, 100.0, LocalDateTime.now());
        RoomBookingResponse response2 = new RoomBookingResponse(2L, 2L, "User2", 2L, "Room2", LocalDate.now().plusDays(3), LocalDate.now().plusDays(4), RoomBookingStatus.PENDIENTE, 200.0, LocalDateTime.now());
        when(roomBookingService.findAll()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/v1/room-bookings"))
                .andExpect(status().isOk());

        verify(roomBookingService).findAll();

    }

    @Test
    void getAll_error() throws Exception {
        when(roomBookingService.findAll()).thenThrow(new RuntimeException("Error interno"));

        mockMvc.perform(get("/api/v1/room-bookings"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void validarReserva_success() throws Exception {
        doNothing().when(roomBookingService).validarReserva(99L, 7L);

        mockMvc.perform(post("/api/v1/room-bookings/validar")
                        .param("idReserva", "99")
                        .param("idRecepcionista", "7"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reserva validada correctamente"));

        verify(roomBookingService).validarReserva(99L, 7L);
    }

    @Test
    void validarReserva_error_whenServiceThrows() throws Exception {
        doThrow(new IllegalArgumentException("Usuario no autorizado"))
                .when(roomBookingService).validarReserva(99L, 7L);

        mockMvc.perform(post("/api/v1/room-bookings/validar")
                        .param("idReserva", "99")
                        .param("idRecepcionista", "7"))
                .andExpect(status().isBadRequest());

        verify(roomBookingService).validarReserva(99L, 7L);
    }



}
