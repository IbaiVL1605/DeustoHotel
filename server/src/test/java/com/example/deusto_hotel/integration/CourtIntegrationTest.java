package com.example.deusto_hotel.integration;

import com.example.deusto_hotel.dto.CourtAvailabilityDTO;
import com.example.deusto_hotel.dto.WeekAvailability;
import com.example.deusto_hotel.model.CourtType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class CourtIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // =========================================================================
    // TESTS PARA: GET /api/v1/courts/available
    // =========================================================================

    @Test
    public void getAvailableCourts_sinParametros_devuelveTodos() {
        // Sin parámetros → entra en la rama findAvailableByTypeAndWeek(null, null)
        // Debe devolver las pistas DISPONIBLES agrupadas por tipo
        // En AppStartup hay 3 pistas DISPONIBLES (Tenis, Padel, Futbol) y 1 BLOQUEADA
        // (Piscina)

        ParameterizedTypeReference<List<CourtAvailabilityDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<CourtAvailabilityDTO>> response = restTemplate.exchange(
                "/api/v1/courts/available",
                HttpMethod.GET,
                null,
                responseType);

        assertEquals(200, response.getStatusCode().value());
        List<CourtAvailabilityDTO> result = response.getBody();
        assertNotNull(result);
        // Hay 3 tipos con pistas DISPONIBLES: TENIS, PADEL, FUTBOL
        assertEquals(3, result.size());
    }

    @Test
    public void getAvailableCourts_conTipo_filtraPorTipo() {
        // Con tipo=TENIS → filtra solo pistas de tenis

        ParameterizedTypeReference<List<CourtAvailabilityDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<CourtAvailabilityDTO>> response = restTemplate.exchange(
                "/api/v1/courts/available?tipo={tipo}",
                HttpMethod.GET,
                null,
                responseType,
                "TENIS");

        assertEquals(200, response.getStatusCode().value());
        List<CourtAvailabilityDTO> result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Todos los resultados deben ser de tipo TENIS
        result.forEach(dto -> assertEquals(CourtType.TENIS, dto.tipo()));
    }

    @Test
    public void getAvailableCourts_conFecha_devuelveDisponiblesParaEsaFecha() {
        // Usar una fecha futura sin reservas → debe devolver las pistas disponibles
        String fechaFutura = LocalDate.now().plusDays(30).toString();

        ParameterizedTypeReference<List<CourtAvailabilityDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<CourtAvailabilityDTO>> response = restTemplate.exchange(
                "/api/v1/courts/available?fecha={fecha}",
                HttpMethod.GET,
                null,
                responseType,
                fechaFutura);

        assertEquals(200, response.getStatusCode().value());
        List<CourtAvailabilityDTO> result = response.getBody();
        assertNotNull(result);
        // Para una fecha sin reservas, las pistas disponibles no deberían tener
        // reservas
        for (CourtAvailabilityDTO dto : result) {
            assertTrue(dto.reservas().isEmpty(),
                    "Para fecha sin reservas, no debería haber reservas en " + dto.tipo());
        }
    }

    @Test
    public void getAvailableCourts_conTipoYFecha_filtraAmbos() {
        // Con tipo=TENIS y fecha con reserva conocida (now+2 es la reserva de court1 en
        // AppStartup)
        String fechaConReserva = LocalDate.now().plusDays(2).toString();

        ParameterizedTypeReference<List<CourtAvailabilityDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<CourtAvailabilityDTO>> response = restTemplate.exchange(
                "/api/v1/courts/available?tipo={tipo}&fecha={fecha}",
                HttpMethod.GET,
                null,
                responseType,
                "TENIS",
                fechaConReserva);

        assertEquals(200, response.getStatusCode().value());
        List<CourtAvailabilityDTO> result = response.getBody();
        assertNotNull(result);
        // Solo debe devolver resultados de tipo TENIS
        assertEquals(1, result.size());
        assertEquals(CourtType.TENIS, result.get(0).tipo());
        // En esa fecha hay 1 reserva de tenis (courtBooking1)
        assertEquals(1, result.get(0).reservas().size());
    }

    @Test
    public void getAvailableCourts_conSemana_devuelveReservasDeLaSemana() {
        // Con tipo=PADEL y semana=1

        ParameterizedTypeReference<List<CourtAvailabilityDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<CourtAvailabilityDTO>> response = restTemplate.exchange(
                "/api/v1/courts/available?tipo={tipo}&semana={semana}",
                HttpMethod.GET,
                null,
                responseType,
                "PADEL",
                "1");

        assertEquals(200, response.getStatusCode().value());
        List<CourtAvailabilityDTO> result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Todos son de tipo PADEL
        result.forEach(dto -> assertEquals(CourtType.PADEL, dto.tipo()));
    }

    // =========================================================================
    // TESTS PARA: GET /api/v1/courts/weekly-availability
    // =========================================================================

    @Test
    public void getWeeklyAvailability_sinTipo_devuelveTodasLasPistas() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        ParameterizedTypeReference<List<WeekAvailability>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<WeekAvailability>> response = restTemplate.exchange(
                "/api/v1/courts/weekly-availability?year={year}&month={month}",
                HttpMethod.GET,
                null,
                responseType,
                String.valueOf(year),
                String.valueOf(month));

        assertEquals(200, response.getStatusCode().value());
        List<WeekAvailability> result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        // Cada semana debe tener días
        result.forEach(week -> assertFalse(week.days().isEmpty()));
    }

    @Test
    public void getWeeklyAvailability_conTipoTenis_filtra() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        ParameterizedTypeReference<List<WeekAvailability>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<WeekAvailability>> response = restTemplate.exchange(
                "/api/v1/courts/weekly-availability?year={year}&month={month}&tipo={tipo}",
                HttpMethod.GET,
                null,
                responseType,
                String.valueOf(year),
                String.valueOf(month),
                "TENIS");

        assertEquals(200, response.getStatusCode().value());
        List<WeekAvailability> result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void getWeeklyAvailability_conTipoInvalido_devuelveTodasIgual() {
        // Un tipo inválido se trata como null → devuelve todas las pistas
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        ParameterizedTypeReference<List<WeekAvailability>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<List<WeekAvailability>> response = restTemplate.exchange(
                "/api/v1/courts/weekly-availability?year={year}&month={month}&tipo={tipo}",
                HttpMethod.GET,
                null,
                responseType,
                String.valueOf(year),
                String.valueOf(month),
                "TIPO_INVENTADO");

        assertEquals(200, response.getStatusCode().value());
        List<WeekAvailability> result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
