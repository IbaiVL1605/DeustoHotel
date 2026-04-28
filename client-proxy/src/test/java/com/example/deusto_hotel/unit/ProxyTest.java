package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomDisponibleResponse;
import com.example.deusto_hotel.dto.RoomDisponiblesSimplesResponse;
import com.example.deusto_hotel.dto.RoomDisponiblesSuitResponse;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    void login_exito() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"mensaje\":\"Sesion iniciada correctamente\",\"usuario\":{\"id\":1,\"nombre\":\"Juan\",\"email\":\"juan@email.com\",\"rol\":\"CLIENT\",\"bloqueado\":false,\"creadoEn\":\"2026-04-25T10:00:00\"}}");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn((HttpResponse) response);

        var result = proxy.login("juan@email.com", "1234");

        assertEquals(1L, result.id());
        assertEquals("Juan", result.nombre());
        assertEquals("juan@email.com", result.email());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void login_error_contrasenaIncorrecta() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(401);
        when(response.body()).thenReturn("Contrasena incorrecta");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn((HttpResponse) response);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> proxy.login("juan@email.com", "mal")
        );

        assertEquals("Contrasena incorrecta", ex.getMessage());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void login_error_correoIncorrecto() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(404);
        when(response.body()).thenReturn("Usuario no encontrado");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn((HttpResponse) response);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> proxy.login("noexiste@email.com", "1234")
        );

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void login_error_respuestaSinUsuario() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"mensaje\":\"Sesion iniciada correctamente\"}");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn((HttpResponse) response);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> proxy.login("juan@email.com", "1234")
        );

        assertEquals("Respuesta de login invalida", ex.getMessage());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any());
    }

    @Test
    void login_error_sinMensajeBackend() throws Exception {
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(500);
        when(response.body()).thenReturn(" ");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn((HttpResponse) response);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> proxy.login("juan@email.com", "1234")
        );

        assertEquals("No se pudo iniciar sesion", ex.getMessage());
        verify(httpClient, times(1)).send(any(HttpRequest.class), any());
    }

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

    @Test
    void testGetHabitacionesDisponibles_Exito() throws Exception {
        // GIVEN: JSON con SUITE, INDIVIDUAL y DOBLE
        String jsonResponse = "[" +
                "  {\"tipo\": \"SUITE\", \"suites\": [{\"capacidad\": 2, \"precioPorNoche\": 200, \"id\": 1}]}," +
                "  {\"tipo\": \"INDIVIDUAL\", \"numero_disponibles\": 5}," +
                "  {\"tipo\": \"DOBLE\", \"numero_disponibles\": 3}" +
                "]";

        // Configuramos el mock para que devuelva 200 OK
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(jsonResponse);

        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        // WHEN
        ArrayList<RoomDisponibleResponse> resultado = proxy.getHabitacionesDisponibles(
                LocalDate.now(), LocalDate.now().plusDays(1)
        );

        // THEN
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals(RoomType.SUITE, resultado.get(0).getTipo());
        assertEquals(RoomType.INDIVIDUAL, resultado.get(1).getTipo());
        assertEquals(RoomType.DOBLE, resultado.get(2).getTipo());
    }

    @Test
    void testGetHabitacionesDisponibles_ErrorServidor() throws Exception {
        // GIVEN: El servidor devuelve un error 500
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("Internal Server Error");

        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        // WHEN & THEN: Verificamos que se lanza la RuntimeException con el mensaje esperado
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            proxy.getHabitacionesDisponibles(LocalDate.now(), LocalDate.now().plusDays(1));
        });

        assertTrue(exception.getMessage().contains("Error al obtener habitaciones disponibles"));
        assertTrue(exception.getMessage().contains("Internal Server Error"));
    }
}
