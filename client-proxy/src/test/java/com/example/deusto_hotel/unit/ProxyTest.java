package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.model.RoomType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.deusto_hotel.proxy.Proxy;
import org.springframework.http.ResponseEntity;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ProxyTest {

    @Mock
    HttpClient httpClient;

    @InjectMocks
    Proxy proxy;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(201);

        when(httpClient.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class)
        )).thenReturn((HttpResponse) response);

        proxy.signup("Juan López", "juan@email.com", "juan123");

        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void shouldNotRegisterUserIfEmailAlreadyExists() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(400);
        when(response.body()).thenReturn("Error: Email already in use");

        when(httpClient.send(any(HttpRequest.class), any()))
                .thenReturn((HttpResponse) response);

        assertThrows(RuntimeException.class, () -> {
            proxy.signup("Juan López", "juan@email.com", "juan123");
        });

        // Verificamos que, aun fallando, se intentó hacer la petición una vez
        verify(httpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void deleteCourtBooking_exito() throws Exception {

        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);


        when(httpClient.send(any(HttpRequest.class), any()))
                .thenReturn((HttpResponse) response);

        proxy.deleteCourtBooking(1L);

        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void deleteCourtBooking_error() throws Exception {

        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(500);
        when(response.body()).thenReturn("Error backend");

        when(httpClient.send(any(HttpRequest.class), any()))
                .thenReturn((HttpResponse) response);

        assertThrows(RuntimeException.class, () ->
                proxy.deleteCourtBooking(1L)
        );

        verify(httpClient).send(any(HttpRequest.class), any());
    }

    //Test delete room
    @Test
    void deleteRoomBooking_exito() throws Exception {

        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(204);


        when(httpClient.send(any(HttpRequest.class), any()))
                .thenReturn((HttpResponse) response);

        proxy.deleteRoomBooking(1L, 10L);

        verify(httpClient).send(any(HttpRequest.class), any());

    }

    @Test
    void deleteRoomBooking_error() throws Exception {

        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(500);
        when(response.body()).thenReturn("Error backend");

        when(httpClient.send(any(HttpRequest.class), any()))
                .thenReturn((HttpResponse) response);

        assertThrows(RuntimeException.class, () ->
                proxy.deleteRoomBooking(1L, 10L)
        );

        verify(httpClient).send(any(HttpRequest.class), any());

    }

    @Test
    void crearReserva_exito() throws Exception {

        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("OK");

        when(httpClient.<String>send(any(HttpRequest.class), any()))
                .thenReturn(response);

        List<RoomBookingRequest> requests = List.of(
                new RoomBookingRequest(
                        RoomType.INDIVIDUAL,
                        1L,
                        1,
                        1L,
                        LocalDate.now(),
                        LocalDate.now().plusDays(2)
                )
        );

        ResponseEntity<String> result = proxy.crearReserva(requests);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("OK", result.getBody());

        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    void crearReserva_error() throws Exception {

        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(400);
        when(response.body()).thenReturn("Error en la petición");

        when(httpClient.<String>send(any(HttpRequest.class), any()))
                .thenReturn(response);

        List<RoomBookingRequest> requests = List.of(
                new RoomBookingRequest(
                        RoomType.INDIVIDUAL,
                        1L,
                        1,
                        1L,
                        LocalDate.now(),
                        LocalDate.now().plusDays(2)
                )
        );

        ResponseEntity<String> result = proxy.crearReserva(requests);

        assertEquals(400, result.getStatusCode().value());
        assertEquals("Error en la petición", result.getBody());
    }
}
