package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.dto.CourtResponse;
import com.example.deusto_hotel.dto.WeekAvailability;
import com.example.deusto_hotel.model.CourtType;
import com.example.deusto_hotel.model.CourtStatus;

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
                                () -> proxy.login("juan@email.com", "mal"));

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
                                () -> proxy.login("noexiste@email.com", "1234"));

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
                                () -> proxy.login("juan@email.com", "1234"));

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
                                () -> proxy.login("juan@email.com", "1234"));

                assertEquals("No se pudo iniciar sesion", ex.getMessage());
                verify(httpClient, times(1)).send(any(HttpRequest.class), any());
        }

        @Test
        void login_error_usuarioNullField() throws Exception {
                HttpResponse<String> response = mock(HttpResponse.class);

                when(response.statusCode()).thenReturn(200);
                when(response.body()).thenReturn("{\"mensaje\":\"Sesion iniciada correctamente\", \"usuario\": null }");

                when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                .thenReturn((HttpResponse) response);

                IllegalArgumentException ex = assertThrows(
                                IllegalArgumentException.class,
                                () -> proxy.login("juan@email.com", "1234"));

                assertEquals("Respuesta de login invalida", ex.getMessage());
                verify(httpClient, times(1)).send(any(HttpRequest.class), any());
        }

        @Test
        void login_error_bodyIsNull() throws Exception {
                HttpResponse<String> response = mock(HttpResponse.class);

                when(response.statusCode()).thenReturn(500);
                when(response.body()).thenReturn(null);

                when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                .thenReturn((HttpResponse) response);

                IllegalArgumentException ex = assertThrows(
                                IllegalArgumentException.class,
                                () -> proxy.login("juan@email.com", "1234"));

                assertEquals("No se pudo iniciar sesion", ex.getMessage());
                verify(httpClient, times(1)).send(any(HttpRequest.class), any());
        }

        @Test
        void login_error_statusBelow200() throws Exception {
                HttpResponse<String> response = mock(HttpResponse.class);

                when(response.statusCode()).thenReturn(199); // < 200, first part of condition false
                when(response.body()).thenReturn("Informational response");

                when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                .thenReturn((HttpResponse) response);

                IllegalArgumentException ex = assertThrows(
                                IllegalArgumentException.class,
                                () -> proxy.login("juan@email.com", "1234"));

                assertEquals("Informational response", ex.getMessage());
                verify(httpClient, times(1)).send(any(HttpRequest.class), any());
        }

        @Test
        void shouldRegisterUserSuccessfully() throws Exception {

                HttpResponse<String> response = mock(HttpResponse.class);
                when(response.statusCode()).thenReturn(201);

                when(httpClient.send(
                                any(HttpRequest.class),
                                any(HttpResponse.BodyHandler.class))).thenReturn((HttpResponse) response);

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

                assertThrows(RuntimeException.class, () -> proxy.deleteCourtBooking(1L));

                verify(httpClient).send(any(HttpRequest.class), any());
        }

        // Test delete room
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

                assertThrows(RuntimeException.class, () -> proxy.deleteRoomBooking(1L, 10L));

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
                                                LocalDate.now().plusDays(2)));

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
                                                LocalDate.now().plusDays(2)));

                ResponseEntity<String> result = proxy.crearReserva(requests);

                assertEquals(400, result.getStatusCode().value());
                assertEquals("Error en la petición", result.getBody());
        }

        @Test
        void testGetHabitacionesDisponibles_Exito() throws Exception {
                // GIVEN: JSON con SUITE, INDIVIDUAL y DOBLE
                String jsonResponse = "[" +
                                "  {\"tipo\": \"SUITE\", \"suites\": [{\"capacidad\": 2, \"precioPorNoche\": 200, \"id\": 1}]},"
                                +
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
                                LocalDate.now(), LocalDate.now().plusDays(1));

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

                // WHEN & THEN: Verificamos que se lanza la RuntimeException con el mensaje
                // esperado
                RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                        proxy.getHabitacionesDisponibles(LocalDate.now(), LocalDate.now().plusDays(1));
                });

                assertTrue(exception.getMessage().contains("Error al obtener habitaciones disponibles"));
                assertTrue(exception.getMessage().contains("Internal Server Error"));
        }

        @Test
        void getCourts_exito_sinFiltro() throws Exception {
                // 1. Prepara el JSON que simula la respuesta del servidor
                String jsonResponse = "[" +
                                "{\"id\":1, \"nombre\":\"Pista 1\", \"tipo\":\"TENIS\", \"precioPorHora\":20.0, \"estado\":\"DISPONIBLE\"},"
                                +
                                "{\"id\":2, \"nombre\":\"Pista 2\", \"tipo\":\"PADEL\", \"precioPorHora\":15.0, \"estado\":\"DISPONIBLE\"}"
                                +
                                "]";

                // 2. Mock del HttpResponse
                HttpResponse<String> mockResponse = mock(HttpResponse.class);
                when(mockResponse.body()).thenReturn(jsonResponse);

                // 3. Configura httpClient para devolver el mock
                when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                .thenReturn((HttpResponse) mockResponse);

                // 4. Llama al método (tipo = null → sin filtro)
                List<CourtResponse> resultado = proxy.getCourts(null);

                // 5. Verifica
                assertNotNull(resultado);
                assertEquals(2, resultado.size());
                assertEquals("Pista 1", resultado.get(0).nombre());
                assertEquals(CourtType.TENIS, resultado.get(0).tipo());
                verify(httpClient).send(any(HttpRequest.class), any());
        }

        @Test
        void getCourts_exito_conFiltroTipo() throws Exception {
                String jsonResponse = "[" +
                                "{\"id\":1, \"nombre\":\"Pista Tenis 1\", \"tipo\":\"TENIS\", \"precioPorHora\":20.0, \"estado\":\"DISPONIBLE\"}"
                                +
                                "]";

                HttpResponse<String> mockResponse = mock(HttpResponse.class);
                when(mockResponse.body()).thenReturn(jsonResponse);

                when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                .thenReturn((HttpResponse) mockResponse);

                List<CourtResponse> resultado = proxy.getCourts("TENIS");

                assertNotNull(resultado);
                assertEquals(1, resultado.size());
                assertEquals(CourtType.TENIS, resultado.get(0).tipo());
                verify(httpClient).send(any(HttpRequest.class), any());
        }

        @Test
        void getCourtsWeeklyAvailability_exito() throws Exception {
                String jsonResponse = "[" +
                                "{" +
                                "  \"weekNumber\": 1," +
                                "  \"days\": [" +
                                "    {" +
                                "      \"date\": \"2026-05-01\"," +
                                "      \"slots\": [" +
                                "        {" +
                                "          \"start\": \"9:00\"," +
                                "          \"end\": \"10:00\"," +
                                "          \"availableCourts\": [" +
                                "            {\"id\":1, \"nombre\":\"Pista 1\", \"tipo\":\"TENIS\", \"precioPorHora\":20.0, \"estado\":\"DISPONIBLE\"}"
                                +
                                "          ]" +
                                "        }" +
                                "      ]" +
                                "    }" +
                                "  ]" +
                                "}" +
                                "]";

                HttpResponse<String> mockResponse = mock(HttpResponse.class);
                when(mockResponse.body()).thenReturn(jsonResponse);

                when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                .thenReturn((HttpResponse) mockResponse);

                List<WeekAvailability> resultado = proxy.getCourtsWeeklyAvailability(2026, 5, null);

                assertNotNull(resultado);
                assertEquals(1, resultado.size());
                assertEquals(1, resultado.get(0).weekNumber());
                assertFalse(resultado.get(0).days().isEmpty());
                verify(httpClient).send(any(HttpRequest.class), any());
        }

        @Test
        void getCourtsWeeklyAvailability_conFiltroTipo() throws Exception {
                // Mismo patrón: JSON + mock + llamada con tipo = "PADEL"
                String jsonResponse = "[{\"weekNumber\": 1, \"days\": []}]";

                HttpResponse<String> mockResponse = mock(HttpResponse.class);
                when(mockResponse.body()).thenReturn(jsonResponse);

                when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                .thenReturn((HttpResponse) mockResponse);

                List<WeekAvailability> resultado = proxy.getCourtsWeeklyAvailability(2026, 5, "PADEL");

                assertNotNull(resultado);
                assertEquals(1, resultado.size());
                verify(httpClient).send(any(HttpRequest.class), any());
        }

        @Test
        void getCourtsAvailable_exito() throws Exception {
                String jsonResponse = "[" +
                                "{\"id\":1, \"nombre\":\"Pista 1\", \"tipo\":\"TENIS\", \"precioPorHora\":20.0, \"estado\":\"DISPONIBLE\"}"
                                +
                                "]";

                HttpResponse<String> mockResponse = mock(HttpResponse.class);
                when(mockResponse.body()).thenReturn(jsonResponse);

                when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                .thenReturn((HttpResponse) mockResponse);

                List<?> resultado = proxy.getCourtsAvailable("TENIS", "2026-05-01", 1);

                assertNotNull(resultado);
                assertEquals(1, resultado.size());
                verify(httpClient).send(any(HttpRequest.class), any());
        }

        @Test
        void getCourtsAvailable_sinParametros() throws Exception {
                String jsonResponse = "[]";

                HttpResponse<String> mockResponse = mock(HttpResponse.class);
                when(mockResponse.body()).thenReturn(jsonResponse);

                when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                                .thenReturn((HttpResponse) mockResponse);

                List<?> resultado = proxy.getCourtsAvailable(null, null, null);

                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
                verify(httpClient).send(any(HttpRequest.class), any());
        }

    @Test
    void crearHabitacionSuccessfully() throws Exception{
        RoomRequest request = new RoomRequest("777", RoomType.SUITE, 2, 200.0);
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(201);
        when(response.body()).thenReturn("OK");

        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        proxy.crearHabitacion(request);

        verify(httpClient, times(1))
                .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void crearHabitacionError() throws Exception{
        RoomRequest request = new RoomRequest("777", RoomType.SUITE, 2, 200.0);
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(400);
        when(response.body()).thenReturn("Error creando habitación");

        when(httpClient.send(any(), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        assertThrows(IllegalArgumentException.class, () -> {
            proxy.crearHabitacion(request);
        });
    }

    @Test
    void getRoomBookingByClientIdSuccessfully() throws Exception{
        Long clienteId = 1L;
        HttpResponse<String> response = mock(HttpResponse.class);

        String json = """
            [
              {
                "id": 1,
                "clienteId": 1,
                "clienteNombre": "Juan López",
                "habitacionId": 101,
                "habitacionNumero": "101A",
                "checkIn": "2025-05-01",
                "checkOut": "2025-05-05",
                "estado": "CONFIRMADA",
                "precioTotal": 500.0,
                "creadaEn": "2025-04-01T10:00:00"
              }
            ]
            """;

        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(json);

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        List<RoomBookingResponse> result = proxy.getRoomBookingsByClienteId(clienteId);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(httpClient).send(any(), any());
    }

    @Test
    void getRoomBookingByClientIdError() throws Exception{
        Long clienteId = 1L;
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.statusCode()).thenReturn(500);
        when(response.body()).thenReturn("Error interno");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        assertThrows(RuntimeException.class, () -> {
            proxy.getRoomBookingsByClienteId(clienteId);
        });
    }
}
