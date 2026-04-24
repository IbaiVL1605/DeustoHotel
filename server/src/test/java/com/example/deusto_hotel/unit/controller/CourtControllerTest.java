package com.example.deusto_hotel.unit.controller;

import com.example.deusto_hotel.controller.CourtController;
import com.example.deusto_hotel.dto.CourtAvailabilityDTO;
import com.example.deusto_hotel.dto.WeekAvailability;
import com.example.deusto_hotel.model.CourtType;
import com.example.deusto_hotel.service.CourtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourtController.class)
public class CourtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourtService courtService;

    // ===================================================================================
    // TESTS PARA: GET /api/v1/courts/available
    // ===================================================================================

    @Test
    public void testGetAvailableCourts_ConTipoYFecha() throws Exception {
        // Arrange
        String tipo = "TENIS";
        String fecha = "2026-05-15";

        CourtAvailabilityDTO dtoTenis = new CourtAvailabilityDTO(CourtType.TENIS, new ArrayList<>());
        CourtAvailabilityDTO dtoPadel = new CourtAvailabilityDTO(CourtType.PADEL, new ArrayList<>());

        // El mock devuelve ambos, pero el controlador filtrará por "TENIS"
        when(courtService.findAvailableByDate(fecha)).thenReturn(List.of(dtoTenis, dtoPadel));

        // Act & Assert
        mockMvc.perform(get("/api/v1/courts/available")
                        .param("tipo", tipo)
                        .param("fecha", fecha)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("TENIS"));

        verify(courtService, times(1)).findAvailableByDate(fecha);
    }

    @Test
    public void testGetAvailableCourts_SoloConTipoYSemana() throws Exception {
        // Arrange
        String tipo = "PADEL";
        Integer semana = 2;

        CourtAvailabilityDTO dtoPadel = new CourtAvailabilityDTO(CourtType.PADEL, new ArrayList<>());
        when(courtService.findAvailableByTypeAndWeek(tipo, semana)).thenReturn(List.of(dtoPadel));

        // Act & Assert
        mockMvc.perform(get("/api/v1/courts/available")
                        .param("tipo", tipo)
                        .param("semana", String.valueOf(semana))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("PADEL"));

        verify(courtService, times(1)).findAvailableByTypeAndWeek(tipo, semana);
    }

    @Test
    public void testGetAvailableCourts_SoloConFecha() throws Exception {
        // Arrange
        String fecha = "2026-06-20";

        // Cambiado a PADEL ya que FRONTON no existe en el enum
        CourtAvailabilityDTO dtoPadel = new CourtAvailabilityDTO(CourtType.PADEL, new ArrayList<>());
        when(courtService.findAvailableByDate(fecha)).thenReturn(List.of(dtoPadel));

        // Act & Assert
        mockMvc.perform(get("/api/v1/courts/available")
                        .param("fecha", fecha)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("PADEL"));

        verify(courtService, times(1)).findAvailableByDate(fecha);
    }

    @Test
    public void testGetAvailableCourts_SinParametros() throws Exception {
        // Arrange
        CourtAvailabilityDTO dtoTenis = new CourtAvailabilityDTO(CourtType.TENIS, new ArrayList<>());
        when(courtService.findAvailableByTypeAndWeek(null, null)).thenReturn(List.of(dtoTenis));

        // Act & Assert
        mockMvc.perform(get("/api/v1/courts/available")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tipo").value("TENIS"));

        verify(courtService, times(1)).findAvailableByTypeAndWeek(null, null);
    }

    // ===================================================================================
    // TESTS PARA: GET /api/v1/courts/weekly-availability
    // ===================================================================================

    @Test
    public void testGetWeeklyAvailability_ExitoConTipoValido() throws Exception {
        // Arrange
        int year = 2026;
        int month = 6;
        String tipo = "TENIS";

        WeekAvailability weekObj = new WeekAvailability(23, new ArrayList<>());
        List<WeekAvailability> mockList = List.of(weekObj);

        when(courtService.findWeeklyAvailability(year, month, CourtType.TENIS)).thenReturn(mockList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courts/weekly-availability")
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month))
                        .param("tipo", tipo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(courtService, times(1)).findWeeklyAvailability(year, month, CourtType.TENIS);
    }

    @Test
    public void testGetWeeklyAvailability_ConTipoInvalido() throws Exception {
        // Arrange
        int year = 2026;
        int month = 6;
        String invalidTipo = "TIPO_QUE_NO_EXISTE";

        WeekAvailability weekObj = new WeekAvailability(23, new ArrayList<>());
        List<WeekAvailability> mockList = List.of(weekObj);

        when(courtService.findWeeklyAvailability(year, month, null)).thenReturn(mockList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courts/weekly-availability")
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month))
                        .param("tipo", invalidTipo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(courtService, times(1)).findWeeklyAvailability(year, month, null);
    }

    @Test
    public void testGetWeeklyAvailability_SinTipo() throws Exception {
        // Arrange
        int year = 2026;
        int month = 8;

        WeekAvailability weekObj = new WeekAvailability(31, new ArrayList<>());
        List<WeekAvailability> mockList = List.of(weekObj);

        when(courtService.findWeeklyAvailability(year, month, null)).thenReturn(mockList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courts/weekly-availability")
                        .param("year", String.valueOf(year))
                        .param("month", String.valueOf(month))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(courtService, times(1)).findWeeklyAvailability(year, month, null);
    }
}