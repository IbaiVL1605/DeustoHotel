package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.CourtController;
import com.example.deusto_hotel.dto.CourtAvailabilityDTO;
import com.example.deusto_hotel.dto.WeekAvailability;
import com.example.deusto_hotel.model.CourtType;
import com.example.deusto_hotel.service.CourtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourtControllerTest {

    @Mock
    private CourtService courtService;

    @InjectMocks
    private CourtController courtController;

    @Test
    void shouldGetAvailableCourtsWithTipoAndFecha() {
        String tipo = "TENIS";
        String fecha = "2026-05-15";
        List<CourtAvailabilityDTO> mockList = new ArrayList<>();
        CourtAvailabilityDTO dto1 = new CourtAvailabilityDTO(CourtType.TENIS, new ArrayList<>());
        mockList.add(dto1);
        
        when(courtService.findAvailableByDate(fecha)).thenReturn(mockList);

        ResponseEntity<List<CourtAvailabilityDTO>> response = courtController.getAvailableCourts(tipo, fecha, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(courtService).findAvailableByDate(fecha);
    }

    @Test
    void shouldGetAvailableCourtsWithTipoAndSemana() {
        String tipo = "PADEL";
        Integer semana = 2;
        List<CourtAvailabilityDTO> mockList = new ArrayList<>();
        
        when(courtService.findAvailableByTypeAndWeek(tipo, semana)).thenReturn(mockList);

        ResponseEntity<List<CourtAvailabilityDTO>> response = courtController.getAvailableCourts(tipo, null, semana);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockList, response.getBody());
        verify(courtService).findAvailableByTypeAndWeek(tipo, semana);
    }

    @Test
    void shouldGetAvailableCourtsNoParams() {
        List<CourtAvailabilityDTO> mockList = new ArrayList<>();
        
        when(courtService.findAvailableByTypeAndWeek(null, null)).thenReturn(mockList);

        ResponseEntity<List<CourtAvailabilityDTO>> response = courtController.getAvailableCourts(null, null, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockList, response.getBody());
        verify(courtService).findAvailableByTypeAndWeek(null, null);
    }

    @Test
    void shouldGetWeeklyAvailability() {
        int year = 2026;
        int month = 6;
        String tipo = "PADEL";
        
        List<WeekAvailability> mockList = new ArrayList<>();
        when(courtService.findWeeklyAvailability(year, month, CourtType.PADEL)).thenReturn(mockList);

        ResponseEntity<List<WeekAvailability>> response = courtController.getWeeklyAvailability(year, month, tipo);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockList, response.getBody());
        verify(courtService).findWeeklyAvailability(year, month, CourtType.PADEL);
    }

    @Test
    void shouldGetWeeklyAvailabilityWithInvalidType() {
        int year = 2026;
        int month = 6;
        String invalidTipo = "INVALID";
        
        List<WeekAvailability> mockList = new ArrayList<>();
        when(courtService.findWeeklyAvailability(year, month, null)).thenReturn(mockList);

        ResponseEntity<List<WeekAvailability>> response = courtController.getWeeklyAvailability(year, month, invalidTipo);

        assertEquals(200, response.getStatusCode().value());
        verify(courtService).findWeeklyAvailability(year, month, null);
    }
}
