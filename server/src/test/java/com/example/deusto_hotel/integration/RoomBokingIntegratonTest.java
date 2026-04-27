package com.example.deusto_hotel.integration;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.model.RoomType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class RoomBokingIntegratonTest {


    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void deleteRoomBooking_ok() {

        String fechaEntrada = LocalDate.now().plusDays(30).toString();
        String fechaSalida = LocalDate.now().plusDays(35).toString();

        ResponseEntity<List> response = restTemplate.getForEntity(
                "/api/v1/rooms/disponibles?fechaEntrada={f1}&fechaSalida={f2}",
                List.class,
                fechaEntrada,
                fechaSalida
        );

        List habitaciones = response.getBody();
        Map suite = (Map) habitaciones.stream()
                .filter(h -> ((Map) h).get("tipo").equals("SUITE"))
                .findFirst()
                .orElseThrow();

        List suites = (List) suite.get("suites");
        Map habitacion = (Map) suites.get(0);

        Long habitacionId = Long.valueOf(habitacion.get("id").toString());

        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.SUITE,
                1L,
                null,
                habitacionId,
                LocalDate.parse(fechaEntrada),
                LocalDate.parse(fechaSalida)
        );

        // ✅ FIX AQUÍ
        ResponseEntity<Void> createResponse = restTemplate.postForEntity(
                "/api/v1/room-bookings",
                List.of(request),
                Void.class
        );

        assertEquals(201, createResponse.getStatusCode().value());

        ResponseEntity<List<RoomBookingResponse>> bookingsResponse = restTemplate.exchange(
                "/api/v1/room-bookings/cliente/{clienteId}",
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                1L
        );

        List<RoomBookingResponse> bookings = bookingsResponse.getBody();
        Long bookingId = bookings.stream()
                .filter(b -> habitacionId.equals(b.habitacionId()))
                .findFirst()
                .orElseThrow()
                .id();

        restTemplate.delete(
                "/api/v1/room-bookings/{id}?userId={userId}",
                bookingId,
                1L
        );
    }
}