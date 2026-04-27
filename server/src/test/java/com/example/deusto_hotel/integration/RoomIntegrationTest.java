package com.example.deusto_hotel.integration;

import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.SuitResponse;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class RoomIntegrationTest {
    public record RoomTestDTO(
            RoomType tipo,
            Integer numero_disponibles,
            List<SuitResponse> suites
    ) {}

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getDiponibles_correcto() {
        LocalDate fechaEntrada = LocalDate.now().plusDays(5);
        LocalDate fechaSalida = LocalDate.now().plusDays(7);


        ParameterizedTypeReference<List<RoomTestDTO>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<List<RoomTestDTO>> response = restTemplate.exchange(
                "/api/v1/rooms/disponibles?fechaEntrada={fechaEntrada}&fechaSalida={fechaSalida}",
                HttpMethod.GET,
                null, // No enviamos cuerpo (requestEntity)
                responseType,
                fechaEntrada.toString(),
                fechaSalida.toString()
        );

        assertEquals(200, response.getStatusCode().value());
        List<RoomTestDTO> disponibles = response.getBody();
        assert disponibles != null;
        assertEquals(3, disponibles.size());


    }

    @Test
    public void getDisponibles_incorrect() {
        LocalDate fechaEntrada = LocalDate.now().plusDays(5);
        LocalDate fechaSalida = LocalDate.now().plusDays(3);


        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/rooms/disponibles?fechaEntrada={fechaEntrada}&fechaSalida={fechaSalida}",
                HttpMethod.GET,
                null, // No enviamos cuerpo (requestEntity)
                String.class,
                fechaEntrada.toString(),
                fechaSalida.toString()
        );

        assertEquals(400, response.getStatusCode().value());
    }
    @Test
    public void createBooking_correcto() {

        LocalDate fechaEntrada = LocalDate.now().plusDays(20);
        LocalDate fechaSalida = LocalDate.now().plusDays(25);

        // 1. Obtener habitaciones disponibles
        ParameterizedTypeReference<List<RoomTestDTO>> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<List<RoomTestDTO>> response = restTemplate.exchange(
                "/api/v1/rooms/disponibles?fechaEntrada={fechaEntrada}&fechaSalida={fechaSalida}",
                HttpMethod.GET,
                null,
                responseType,
                fechaEntrada.toString(),
                fechaSalida.toString()
        );

        assertEquals(200, response.getStatusCode().value());

        List<RoomTestDTO> disponibles = response.getBody();
        assert disponibles != null;

        // 2. Buscar una SUITE (más fácil de controlar)
        RoomTestDTO suite = disponibles.stream()
                .filter(r -> r.tipo() == RoomType.SUITE)
                .findFirst()
                .orElseThrow();

        SuitResponse habitacion = suite.suites().get(0);

        // 3. Crear request correcto
        var request = new com.example.deusto_hotel.dto.RoomBookingRequest(
                RoomType.SUITE,
                1L,                // cliente existente
                null,              // obligatorio null para SUITE
                (long) habitacion.id(),
                fechaEntrada,
                fechaSalida
        );

        // 4. POST
        ResponseEntity<Void> createResponse = restTemplate.postForEntity(
                "/api/v1/room-bookings",
                List.of(request),
                Void.class
        );

        assertEquals(201, createResponse.getStatusCode().value());
    }



}
