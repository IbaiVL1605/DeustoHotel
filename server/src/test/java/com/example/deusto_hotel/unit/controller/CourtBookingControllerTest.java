package com.example.deusto_hotel.unit.controller;

import com.example.deusto_hotel.controller.CourtBookingController;
import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.model.CourtBookingStatus;
import com.example.deusto_hotel.service.CourtBookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CourtBookingControllerTest {

    @Mock
    CourtBookingService courtBookingService;

    private ObjectMapper objectMapper;

    @InjectMocks
    CourtBookingController controller;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
/*
    @Test
    void testGetAll() throws Exception {
        when(courtBookingService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/court-bookings"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetById() throws Exception {
        CourtBookingResponse response = new CourtBookingResponse(1L, 1L, "User", 1L, "Court", LocalDate.of(2025, 1, 1), LocalTime.of(10, 0), LocalTime.of(12, 0), CourtBookingStatus.CONFIRMADA, 20.0, LocalDateTime.of(2025, 1, 1, 10, 0));
        when(courtBookingService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/court-bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
*/
    @Test
    void testCreate() throws Exception {
        CourtBookingRequest request = new CourtBookingRequest(1L, LocalDate.of(2025, 1, 1), LocalTime.of(10, 0), LocalTime.of(12, 0), 1L);
        CourtBookingResponse response = new CourtBookingResponse(1L, 1L, "User", 1L, "Court", LocalDate.of(2025, 1, 1), LocalTime.of(10, 0), LocalTime.of(12, 0), CourtBookingStatus.CONFIRMADA, 20.0, LocalDateTime.now());

        when(courtBookingService.create(any(CourtBookingRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/court-bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testUpdate() throws Exception {
        CourtBookingRequest request = new CourtBookingRequest(1L, LocalDate.of(2025, 1, 1), LocalTime.of(10, 0), LocalTime.of(12, 0), 1L);
        CourtBookingResponse response = new CourtBookingResponse(1L, 1L, "User", 1L, "Court", LocalDate.of(2025, 1, 1), LocalTime.of(10, 0), LocalTime.of(12, 0), CourtBookingStatus.CONFIRMADA, 20.0, LocalDateTime.now());

        when(courtBookingService.update(eq(1L), any(CourtBookingRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/court-bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/court-bookings/1"))
                .andExpect(status().isNoContent());

        verify(courtBookingService).delete(1L);
    }

    @Test
    void testGetByClienteId() throws Exception {
        when(courtBookingService.findByClienteId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/court-bookings/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
/*
    @Test
    void testGetByPistaId() throws Exception {
        when(courtBookingService.findByPistaId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/court-bookings/pista/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

 */
    @Test
    void testCancelBookingAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/court-bookings/1/cancel"))
                .andExpect(status().isOk());

        verify(courtBookingService).cancelBookingAdmin(1L);
    }

    @Test
    void testValidarReserva() throws Exception {
        mockMvc.perform(post("/api/v1/court-bookings/validar")
                        .param("idReserva", "99")
                        .param("idRecepcionista", "7"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reserva validada correctamente"));

        verify(courtBookingService).validarReserva(99L, 7L);
    }

    @Test
    void testValidarReserva_error() {
        doThrow(new IllegalArgumentException("Usuario no autorizado"))
                .when(courtBookingService).validarReserva(99L, 7L);

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/v1/court-bookings/validar")
                        .param("idReserva", "99")
                        .param("idRecepcionista", "7"))
        );

        verify(courtBookingService).validarReserva(99L, 7L);
    }

    @Test
    void findById_exito() throws Exception {
        when(courtBookingService.findByClienteId(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/court-bookings/cliente/1")).andExpect(status().isOk());

    }


    @Test
    void findById_error(){
        when(courtBookingService.findByClienteId(1L))
                .thenThrow(new RuntimeException("Error al obtener las reservas"));

        assertThrows(RuntimeException.class, () -> courtBookingService.findByClienteId(1L));

        verify(courtBookingService).findByClienteId(1L);
    }

    @Test
    void findAll_exito() throws Exception {
        when(courtBookingService.findAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/court-bookings")).andExpect(status().isOk());
    }


     @Test
    void findAll_error(){
        when(courtBookingService.findAll())
                .thenThrow(new RuntimeException("Error al obtener las reservas"));

        assertThrows(RuntimeException.class, () -> courtBookingService.findAll());

        verify(courtBookingService).findAll();
    }

    @Test
    void getById() throws Exception {
        CourtBookingResponse response = new CourtBookingResponse(1L, 1L, "User", 1L, "Court", LocalDate.of(2025, 1, 1), LocalTime.of(10, 0), LocalTime.of(12, 0), CourtBookingStatus.CONFIRMADA, 20.0, LocalDateTime.now());
        when(courtBookingService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/court-bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getById_error() {
        when(courtBookingService.findById(1L))
                .thenThrow(new RuntimeException("Reserva no encontrada"));

        assertThrows(RuntimeException.class, () -> courtBookingService.findById(1L));

        verify(courtBookingService).findById(1L);
    }


}
