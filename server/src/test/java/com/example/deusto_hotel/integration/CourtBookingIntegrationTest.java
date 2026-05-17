package com.example.deusto_hotel.integration;

import com.example.deusto_hotel.dto.CourtBookingResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CourtBookingIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Order(1)
    void validarReservaPista_usuarioNoEncontrado() {
        Long idReservaPendiente = obtenerIdReservaPorEstadoPista("PENDIENTE");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/court-bookings/validar?idReserva={idReserva}&idRecepcionista={idRecepcionista}",
                null,
                String.class,
                idReservaPendiente,
                999999L
        );

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Usuario no encontrado"));
    }

    @Test
    @Order(2)
    void validarReservaPista_usuarioNoAutorizado() {
        Long idReservaPendiente = obtenerIdReservaPorEstadoPista("PENDIENTE");
        Long idCliente = loginYObtenerId("juan@email.com", "juan123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/court-bookings/validar?idReserva={idReserva}&idRecepcionista={idRecepcionista}",
                null,
                String.class,
                idReservaPendiente,
                idCliente
        );

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Usuario no autorizado"));
    }

    @Test
    @Order(3)
    void validarReservaPista_noEstaPendiente() {
        Long idRecepcionista = loginRecepcionistaYObtenerId();
        Long idReservaConfirmada = obtenerIdReservaPorEstadoPista("CONFIRMADA");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/court-bookings/validar?idReserva={idReserva}&idRecepcionista={idRecepcionista}",
                null,
                String.class,
                idReservaConfirmada,
                idRecepcionista
        );

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().contains("La reserva no está en estado pendiente"));
    }

    @Test
    @Order(4)
    void validarReservaPista_reservaNoEncontrada() {
        Long idRecepcionista = loginRecepcionistaYObtenerId();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/court-bookings/validar?idReserva={idReserva}&idRecepcionista={idRecepcionista}",
                null,
                String.class,
                999999L,
                idRecepcionista
        );

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Reserva no encontrada"));
    }

    @Test
    @Order(5)
    void validarReservaPista_ok() {
        Long idRecepcionista = loginRecepcionistaYObtenerId();

        ResponseEntity<List<CourtBookingResponse>> allResponse = restTemplate.exchange(
                "/api/v1/court-bookings",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(200, allResponse.getStatusCode().value());
        List<CourtBookingResponse> reservas = allResponse.getBody();
        assertNotNull(reservas);

        CourtBookingResponse pendiente = reservas.stream()
                .filter(r -> "PENDIENTE".equals(r.estado().name()))
                .findFirst()
                .orElseThrow();

        ResponseEntity<String> validarResponse = restTemplate.postForEntity(
                "/api/v1/court-bookings/validar?idReserva={idReserva}&idRecepcionista={idRecepcionista}",
                null,
                String.class,
                pendiente.id(),
                idRecepcionista
        );

        assertEquals(200, validarResponse.getStatusCode().value());
        assertEquals("Reserva validada correctamente", validarResponse.getBody());

        ResponseEntity<List<CourtBookingResponse>> afterResponse = restTemplate.exchange(
                "/api/v1/court-bookings",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        CourtBookingResponse actualizada = afterResponse.getBody().stream()
                .filter(r -> r.id().equals(pendiente.id()))
                .findFirst()
                .orElseThrow();

        assertEquals("CONFIRMADA", actualizada.estado().name());
    }

    private Long obtenerIdReservaPorEstadoPista(String estado) {
        ResponseEntity<List<CourtBookingResponse>> response = restTemplate.exchange(
                "/api/v1/court-bookings",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(200, response.getStatusCode().value());
        List<CourtBookingResponse> reservas = response.getBody();
        assertNotNull(reservas);

        return reservas.stream()
                .filter(r -> estado.equals(r.estado().name()))
                .findFirst()
                .orElseThrow()
                .id();
    }

    private Long loginRecepcionistaYObtenerId() {
        return loginYObtenerId("maria@deusto.com", "maria123");
    }

    private Long loginYObtenerId(String correo, String contrasena) {
        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                "/api/v1/users/login?correo={correo}&contrasena={contrasena}",
                null,
                Map.class,
                correo,
                contrasena
        );

        assertEquals(200, loginResponse.getStatusCode().value());
        assertNotNull(loginResponse.getBody());

        Map usuario = (Map) loginResponse.getBody().get("usuario");
        Number id = (Number) usuario.get("id");
        return id.longValue();
    }
}

