package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.model.*;

import java.time.LocalTime;

import com.example.deusto_hotel.controller.Controller;
import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.proxy.Proxy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
class ControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private Controller controller;

        @MockitoBean
        private Proxy proxy;

        private ObjectMapper objectMapper = new ObjectMapper()
                        .registerModule(new JavaTimeModule());

        @Test
        void deleteCourtBooking_exito() throws Exception {

                mockMvc.perform(post("/reservas/eliminar/pista/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/reservas"));

                verify(proxy).deleteCourtBooking(1L);
        }

        @Test
        void deleteCourtBookingTest_error() throws Exception {

                doThrow(new RuntimeException("Error"))
                                .when(proxy).deleteCourtBooking(1L);

                mockMvc.perform(post("/reservas/eliminar/pista/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/reservas"));

                verify(proxy).deleteCourtBooking(1L);
        }

        @Test
        void crearReserva_error() throws Exception {

                List<RoomBookingRequest> request = List.of(
                                new RoomBookingRequest(
                                                RoomType.INDIVIDUAL,
                                                999L,
                                                1,
                                                1L,
                                                LocalDate.now(),
                                                LocalDate.now().plusDays(2)));

                mockMvc.perform(post("/reservas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Usuario no logeado"));

                verify(proxy, never()).crearReserva(any());
        }

        @Test
        void crearReserva_exito() throws Exception {

                List<RoomBookingRequest> request = List.of(
                                new RoomBookingRequest(
                                                RoomType.INDIVIDUAL,
                                                999L,
                                                1,
                                                1L,
                                                LocalDate.now(),
                                                LocalDate.now().plusDays(2)));

                ResponseEntity<String> response = ResponseEntity.ok("OK");

                when(proxy.crearReserva(any())).thenReturn(response);

                mockMvc.perform(post("/reservas")
                                .sessionAttr("userId", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().string("OK"));

                verify(proxy).crearReserva(any());
        }

        @Test
        void login_exito() throws Exception {
                UserResponse usuario = new UserResponse(
                                1L,
                                "Juan",
                                "juan@email.com",
                                Role.CLIENT,
                                false,
                                LocalDateTime.now());

                when(proxy.login("juan@email.com", "1234")).thenReturn(usuario);

                mockMvc.perform(get("/api/v1/login")
                                .param("email", "juan@email.com")
                                .param("password", "1234"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/habitaciones/disponibles"))
                                .andExpect(request().sessionAttribute("userId", 1L))
                                .andExpect(request().sessionAttribute("username", "Juan"))
                                .andExpect(request().sessionAttribute("userEmail", "juan@email.com"))
                                .andExpect(request().sessionAttribute("userRole", "CLIENT"));

                verify(proxy).login("juan@email.com", "1234");
        }

        @Test
        void login_exito_admin_redirigeAAdmin() throws Exception {
                UserResponse usuario = new UserResponse(
                                1L,
                                "Admin",
                                "admin@email.com",
                                Role.ADMIN,
                                false,
                                LocalDateTime.now());

                when(proxy.login("admin@email.com", "1234")).thenReturn(usuario);

                mockMvc.perform(get("/api/v1/login")
                                .param("email", "admin@email.com")
                                .param("password", "1234"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin"))
                                .andExpect(request().sessionAttribute("userRole", "ADMIN"));

                verify(proxy).login("admin@email.com", "1234");
        }

        @Test
        void login_error_correoNull() {
                IllegalArgumentException ex = assertThrows(
                                IllegalArgumentException.class,
                                () -> controller.login(new MockHttpSession(), null, "1234"));

                assertEquals("El correo es obligatorio", ex.getMessage());
                verifyNoInteractions(proxy);
        }

        @Test
        void login_error_correoVacio() {
                IllegalArgumentException ex = assertThrows(
                                IllegalArgumentException.class,
                                () -> controller.login(new MockHttpSession(), "", "1234"));

                assertEquals("El correo es obligatorio", ex.getMessage());
                verifyNoInteractions(proxy);
        }

        @Test
        void login_error_passwordNull() {
                IllegalArgumentException ex = assertThrows(
                                IllegalArgumentException.class,
                                () -> controller.login(new MockHttpSession(), "a@gmail.com", null));

                assertEquals("La contraseña es obligatoria", ex.getMessage());
                verifyNoInteractions(proxy);
        }

        @Test
        void login_error_passwordVacio() {
                IllegalArgumentException ex = assertThrows(
                                IllegalArgumentException.class,
                                () -> controller.login(new MockHttpSession(), "a@gmail.com", ""));

                assertEquals("La contraseña es obligatoria", ex.getMessage());
                verifyNoInteractions(proxy);
        }

        @Test
        void login_error_contrasenaIncorrecta() throws Exception {
                when(proxy.login("juan@email.com", "mal"))
                                .thenThrow(new IllegalArgumentException("Contrasena incorrecta"));

                ServletException ex = assertThrows(
                                ServletException.class,
                                () -> mockMvc.perform(get("/api/v1/login")
                                                .param("email", "juan@email.com")
                                                .param("password", "mal")));

                assertNotNull(ex.getCause());
                assertEquals("Contrasena incorrecta", ex.getCause().getMessage());

                verify(proxy).login("juan@email.com", "mal");
        }

        @Test
        void login_error_correoIncorrecto() throws Exception {
                when(proxy.login("noexiste@email.com", "1234"))
                                .thenThrow(new IllegalArgumentException("Usuario no encontrado"));

                ServletException ex = assertThrows(
                                ServletException.class,
                                () -> mockMvc.perform(get("/api/v1/login")
                                                .param("email", "noexiste@email.com")
                                                .param("password", "1234")));

                assertNotNull(ex.getCause());
                assertEquals("Usuario no encontrado", ex.getCause().getMessage());

                verify(proxy).login("noexiste@email.com", "1234");
        }

        // DELETE OK

        @Test
        void deleteBooking_success() throws Exception {

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("userId", 10L);

                mockMvc.perform(post("/reservas/eliminar/1")
                                .session(session))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/reservas"));

                verify(proxy).deleteRoomBooking(1L, 10L);
        }

        // SIN USUARIO

        @Test
        void deleteBooking_noUser() throws Exception {

                mockMvc.perform(post("/reservas/eliminar/1"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));

                verify(proxy, never()).deleteRoomBooking(any(), any());
        }

        // ERROR PROXY

        @Test
        void deleteBooking_error() throws Exception {

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("userId", 10L);

                doThrow(new RuntimeException("Error"))
                                .when(proxy).deleteRoomBooking(1L, 10L);

                mockMvc.perform(post("/reservas/eliminar/1")
                                .session(session))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/reservas"));

                verify(proxy).deleteRoomBooking(1L, 10L);
        }

        // Test usuario registrado
        @Test
        void shouldRegisterUserSuccessfully() throws Exception {
                mockMvc.perform(post("/signup")
                                .param("email", "juan@email.com")
                                .param("password", "1234")
                                .param("nombre", "Juan López"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));

                verify(proxy).signup("Juan López", "juan@email.com", "1234");
        }

        // Test error al registrar usuario
        @Test
        void shouldNotRegisterUserIfEmailAlreadyExists() throws Exception {
                doThrow(new RuntimeException("Error backend"))
                                .when(proxy).signup(anyString(), anyString(), anyString());

                mockMvc.perform(post("/signup")
                                .param("email", "juan@email.com")
                                .param("password", "1234")
                                .param("nombre", "Juan López"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/signup"));

                verify(proxy).signup("Juan López", "juan@email.com", "1234");
        }

        @Test
        void testGetHabitacionesDisponibles_Exito() throws Exception {
                // GIVEN: Una lista que contiene los 3 tipos para cubrir todo el SWITCH
                ArrayList<RoomDisponibleResponse> habitaciones = new ArrayList<>();
                habitaciones.add(new RoomDisponiblesSimplesResponse(RoomType.INDIVIDUAL, 5));
                habitaciones.add(new RoomDisponiblesSimplesResponse(RoomType.DOBLE, 2));
                habitaciones.add(new RoomDisponiblesSuitResponse(RoomType.SUITE, new ArrayList<>()));

                when(proxy.getHabitacionesDisponibles(any(), any())).thenReturn(habitaciones);

                // WHEN & THEN
                mockMvc.perform(get("/habitaciones/disponibles")
                                .param("fechaEntrada", "2026-05-01")
                                .param("fechaSalida", "2026-05-10"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("user/habitaciones"))
                                .andExpect(model().attributeExists("habitacionSimple"))
                                .andExpect(model().attributeExists("habitacionDoble"))
                                .andExpect(model().attributeExists("habitacionSuite")); // Rama SUITE probada
        }

        @Test
        void testGetHabitacionesDisponibles_ErrorProxy() throws Exception {
                // GIVEN: El proxy lanza una excepción (como la que configuramos antes)
                when(proxy.getHabitacionesDisponibles(any(LocalDate.class), any(LocalDate.class)))
                                .thenThrow(new RuntimeException(
                                                "Error al obtener habitaciones disponibles: Server Error"));

                // WHEN & THEN
                mockMvc.perform(get("/habitaciones/disponibles")
                                .param("fechaEntrada", "2026-05-01")
                                .param("fechaSalida", "2026-05-10"))
                                .andExpect(status().isOk()) // El controlador captura el error y devuelve 200 con la
                                                            // vista
                                .andExpect(view().name("user/habitaciones"))
                                .andExpect(model().attributeExists("error")) // Verificamos que se añadió el mensaje de
                                                                             // error
                                .andExpect(model().attribute("error",
                                                "No se han podido cargar las habitaciones en este momento. Inténtelo más tarde."));
        }

        @Test
        void testGetHabitacionesDisponibles_FechaEntradaNull() throws Exception {
                // Para cubrir el "1 de 4" de JaCoCo, probamos explícitamente cuando una es null
                mockMvc.perform(get("/habitaciones/disponibles")
                                .param("fechaSalida", "2026-05-10")) // fechaEntrada es null
                                .andExpect(status().isOk())
                                .andExpect(view().name("user/habitaciones"));

                verifyNoInteractions(proxy);
        }

        @Test
        void testGetHabitacionesDisponibles_FechaSalidaNull() throws Exception {
                // Probamos la otra condición del OR
                mockMvc.perform(get("/habitaciones/disponibles")
                                .param("fechaEntrada", "2026-05-01")) // fechaSalida es null
                                .andExpect(status().isOk())
                                .andExpect(view().name("user/habitaciones"));

                verifyNoInteractions(proxy);
        }

        @Test
        void createCourtBooking_noUser() throws Exception {

                CourtBookingRequest request = new CourtBookingRequest(
                                1L,
                                LocalDate.now(),
                                java.time.LocalTime.of(10, 0),
                                java.time.LocalTime.of(12, 0),
                                null);

                mockMvc.perform(post("/api/v1/court-bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().json(
                                                "{\"status\":\"ERROR\", \"message\":\"Usuario no autenticado\"}"));

                verify(proxy, never()).createCourtBooking(any());
        }

        @Test
        void createCourtBooking_exito() throws Exception {

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("userId", 1L);

                CourtBookingRequest request = new CourtBookingRequest(
                                1L,
                                LocalDate.now(),
                                java.time.LocalTime.of(10, 0),
                                java.time.LocalTime.of(12, 0),
                                null // el controller lo sustituye
                );

                mockMvc.perform(post("/api/v1/court-bookings")
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().json("{\"status\":\"OK\"}"));

                verify(proxy).createCourtBooking(any());
        }

        @Test
        void createCourtBooking_error() throws Exception {

                MockHttpSession session = new MockHttpSession();
                session.setAttribute("userId", 1L);

                doThrow(new RuntimeException("Error backend"))
                                .when(proxy).createCourtBooking(any());

                CourtBookingRequest request = new CourtBookingRequest(
                                1L,
                                LocalDate.now(),
                                java.time.LocalTime.of(10, 0),
                                java.time.LocalTime.of(12, 0),
                                null);

                mockMvc.perform(post("/api/v1/court-bookings")
                                .session(session)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(content().json("{\"status\":\"ERROR\", \"message\":\"Error backend\"}"));

                verify(proxy).createCourtBooking(any());
        }

        @Test
        void getPistas_exito_sinFiltro() throws Exception {
                // Prepara datos de retorno del proxy
                List<WeekAvailability> disponibilidad = List.of(
                                new WeekAvailability(1, List.of(
                                                new DayAvailability(LocalDate.of(2026, 5, 1), List.of(
                                                                new AvailableSlot(
                                                                                LocalTime.of(9, 0),
                                                                                LocalTime.of(10, 0),
                                                                                List.of(new CourtResponse(1L, "Pista 1",
                                                                                                CourtType.TENIS, 20.0,
                                                                                                CourtStatus.DISPONIBLE))))))));

                when(proxy.getCourtsWeeklyAvailability(anyInt(), anyInt(), isNull()))
                                .thenReturn(disponibilidad);

                mockMvc.perform(get("/pistas"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("user/pistas"))
                                .andExpect(model().attributeExists("disponibilidad"))
                                .andExpect(model().attributeExists("year"))
                                .andExpect(model().attributeExists("month"))
                                .andExpect(model().attributeExists("hoy"));

                verify(proxy).getCourtsWeeklyAvailability(anyInt(), anyInt(), isNull());
        }

        @Test
        void getPistas_conFiltroTipoYFecha() throws Exception {
                List<WeekAvailability> disponibilidad = List.of(
                                new WeekAvailability(1, List.of()));

                when(proxy.getCourtsWeeklyAvailability(2026, 6, "PADEL"))
                                .thenReturn(disponibilidad);

                mockMvc.perform(get("/pistas")
                                .param("tipo", "PADEL")
                                .param("year", "2026")
                                .param("month", "6"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("user/pistas"))
                                .andExpect(model().attribute("tipo", "PADEL"))
                                .andExpect(model().attribute("year", 2026))
                                .andExpect(model().attribute("month", 6));

                verify(proxy).getCourtsWeeklyAvailability(2026, 6, "PADEL");
        }

        @Test
        void getPistas_errorProxy() throws Exception {
                when(proxy.getCourtsWeeklyAvailability(anyInt(), anyInt(), isNull()))
                                .thenThrow(new RuntimeException("Error del servidor"));

                ServletException ex = assertThrows(
                                ServletException.class,
                                () -> mockMvc.perform(get("/pistas")));

                assertNotNull(ex.getCause());
                assertEquals("Error del servidor", ex.getCause().getMessage());

                verify(proxy).getCourtsWeeklyAvailability(anyInt(), anyInt(), isNull());
        }

        @Test
        void crearHabitacion() throws Exception {
                RoomBookingRequest request = new RoomBookingRequest(
                                RoomType.INDIVIDUAL,
                                1L,
                                1,
                                1L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(2));

                ResponseEntity<String> response = ResponseEntity.ok("OK");

                when(proxy.crearReserva(any())).thenReturn(response);

                mockMvc.perform(post("/reservas")
                                .sessionAttr("userId", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(List.of(request))))
                                .andExpect(status().isOk())
                                .andExpect(content().string("OK"));

                verify(proxy).crearReserva(any());

        }

        @Test
        void crearHabitacion_error() throws Exception {

                RoomBookingRequest request = new RoomBookingRequest(
                                RoomType.INDIVIDUAL,
                                1L,
                                1,
                                1L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(2));

                when(proxy.crearReserva(any()))
                                .thenThrow(new RuntimeException("Error al crear reserva"));

                assertThrows(Exception.class, () -> {
                        mockMvc.perform(post("/reservas")
                                        .sessionAttr("userId", 1L)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(List.of(request))));
                });

                verify(proxy).crearReserva(any());
        }

        @Test
        void crearReserva_usuarioNoLogeado() throws Exception {

                RoomBookingRequest request = new RoomBookingRequest(
                                RoomType.INDIVIDUAL,
                                1L,
                                1,
                                1L,
                                LocalDate.now(),
                                LocalDate.now().plusDays(2));

                mockMvc.perform(post("/reservas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(List.of(request))))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string("Usuario no logeado"));
        }

        @Test
        void adminPage_exito() throws Exception {
                mockMvc.perform(get("/admin")
                                .sessionAttr("userRole", "ADMIN"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/admin"));
        }

        @Test
        void adminPage_error() throws Exception {
                mockMvc.perform(get("/admin"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }

        @Test
        void adminPage_error_rolIncorrecto() throws Exception {
                mockMvc.perform(get("/admin")
                                .sessionAttr("userRole", "CLIENT"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }

        @Test
        void showMenu_exito() throws Exception {

                mockMvc.perform(get("/menu")
                                .sessionAttr("userRole", "CLIENT"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("auth/menu"));
        }

        @Test
        void showMenu_error() throws Exception {

                mockMvc.perform(get("/menu"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }

        @Test
        void showLoginForm_exito() throws Exception {
                mockMvc.perform(get("/login"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("auth/login"));
        }

        @Test
        void showSignupForm_exito() throws Exception {
                mockMvc.perform(get("/signup"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("auth/signup"));

        }

        @Test
        void verReservas_exito() throws Exception {

                List<RoomBookingResponse> roomBookings = List.of();
                List<CourtBookingResponse> courtBookings = List.of();

                when(proxy.getRoomBookingsByClienteId(1L)).thenReturn(roomBookings);
                when(proxy.getCourtBookingsByClienteId(1L)).thenReturn(courtBookings);

                mockMvc.perform(get("/reservas")
                                .sessionAttr("userId", 1L))
                                .andExpect(status().isOk())
                                .andExpect(view().name("user/reservas"))
                                .andExpect(model().attributeExists("roomBookings"))
                                .andExpect(model().attributeExists("courtBookings"));
        }

        @Test
        void verReservas_fracaso() throws Exception {
                mockMvc.perform(get("/reservas"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/login"));
        }

        @Test
        void getHabitacionesDisponibles_exito() throws Exception {

                LocalDate entrada = LocalDate.of(2025, 5, 1);
                LocalDate salida = LocalDate.of(2025, 5, 3);

                RoomDisponibleResponse responseMock = mock(RoomDisponibleResponse.class);
                when(responseMock.getTipo()).thenReturn(RoomType.INDIVIDUAL);

                ArrayList<RoomDisponibleResponse> mockResponse = new ArrayList<>();
                mockResponse.add(responseMock);

                when(proxy.getHabitacionesDisponibles(entrada, salida))
                                .thenReturn(mockResponse);

                mockMvc.perform(get("/api/habitaciones/disponibles")
                                .param("fechaEntrada", entrada.toString())
                                .param("fechaSalida", salida.toString()))
                                .andExpect(status().isOk());
        }

        @Test
        void getHabitacionesDisponibles_error() throws Exception {

                LocalDate entrada = LocalDate.of(2025, 5, 1);
                LocalDate salida = LocalDate.of(2025, 5, 3);

                when(proxy.getHabitacionesDisponibles(any(), any()))
                                .thenThrow(new RuntimeException("Error test"));

                mockMvc.perform(get("/api/habitaciones/disponibles")
                                .param("fechaEntrada", entrada.toString())
                                .param("fechaSalida", salida.toString()))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        void blockCourtFromAdmin_Success() throws Exception {
                mockMvc.perform(post("/admin/courts/1/block"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin"))
                                .andExpect(flash().attribute("success",
                                                "Pista bloqueada por mantenimiento exitosamente."));

                verify(proxy).blockCourt(1L);
        }

        @Test
        void unblockCourtFromAdmin_Success() throws Exception {
                // Simulamos la interacción desde la interfaz web (botón de desbloquear)
                mockMvc.perform(post("/admin/courts/1/unblock"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/admin"))
                                .andExpect(flash().attribute("success",
                                                "Pista desbloqueada y disponible de nuevo."));

                // Comprobamos que el controlador web ha llamado al método del proxy
                verify(proxy).unblockCourt(1L);
        }

        @Test
        void adminPage_MuestraPistasExito() throws Exception {
                List<CourtResponse> courtsMock = List.of(
                                new CourtResponse(1L, "Pista 1", CourtType.TENIS, 20.0, CourtStatus.DISPONIBLE));

                when(proxy.getCourts(null)).thenReturn(courtsMock);

                mockMvc.perform(get("/admin")
                                .sessionAttr("userRole", "ADMIN"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("admin/admin"))
                                .andExpect(model().attributeExists("courts"));
        }

    @Test
    void updateCourtBooking_Exito() throws Exception {
        Long bookingId = 123L;

        // Usamos thenAnswer para evitar el error de "Only void methods can doNothing()"
        // Esto simula que el proxy procesa la petición correctamente sin importar qué retorne.
        when(proxy.updateCourtBooking(eq(bookingId), any(CourtBookingRequest.class)))
                .thenAnswer(invocation -> null);

        mockMvc.perform(post("/reservas/pista/modificar/{id}", bookingId)
                        .param("pistaId", "1")
                        .param("fecha", "2026-05-20")
                        .param("horaInicio", "10:00:00")
                        .param("horaFin", "11:00:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas"))
                .andExpect(flash().attribute("success", "Reserva actualizada correctamente"));

        // Verificamos que el controlador llamó al proxy pasándole el ID de la URL
        verify(proxy, times(1)).updateCourtBooking(eq(bookingId), any(CourtBookingRequest.class));
    }

    @Test
    void updateCourtBooking_Error() throws Exception {
        Long bookingId = 123L;
        String mensajeError = "La pista ya está ocupada en ese horario o no está disponible";

        // Forzamos al proxy a lanzar una excepción para hacer saltar el bloque catch
        doThrow(new RuntimeException(mensajeError))
                .when(proxy).updateCourtBooking(eq(bookingId), any(CourtBookingRequest.class));

        mockMvc.perform(post("/reservas/pista/modificar/{id}", bookingId)
                        .param("pistaId", "1")
                        .param("fecha", "2026-05-20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas"))
                .andExpect(flash().attribute("error", mensajeError));

        verify(proxy, times(1)).updateCourtBooking(eq(bookingId), any(CourtBookingRequest.class));
    }



        @Test
        void validarReserva_exito() throws Exception {
                when(proxy.validarReserva(99L, 7L)).thenReturn(ResponseEntity.ok("OK"));

                mockMvc.perform(post("/recepcion/validar")
                                .sessionAttr("userId", 7L)
                                .param("idReserva", "99"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/recepcion"))
                                .andExpect(flash().attribute("success", "Reserva validada correctamente."));

                verify(proxy).validarReserva(99L, 7L);
        }

        @Test
        void validarReserva_error() throws Exception {
                when(proxy.validarReserva(99L, 7L)).thenThrow(new RuntimeException("Usuario no autorizado"));

                mockMvc.perform(post("/recepcion/validar")
                                .sessionAttr("userId", 7L)
                                .param("idReserva", "99"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/recepcion"))
                                .andExpect(flash().attribute("error",
                                                "Error al validar la reserva: Usuario no autorizado"));

                verify(proxy).validarReserva(99L, 7L);
        }

        @Test
        void bloquearHabitacion_exito() throws Exception {

                mockMvc.perform(post("/rooms/1/bloquear"))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrlPattern(
                                "/habitaciones/disponibles?fechaEntrada=*&fechaSalida=*"
                        ));

                verify(proxy).bloquearHabitacion(1L);
        }

        @Test
        void bloquearHabitacion_error() throws Exception {

                doThrow(new RuntimeException("Error"))
                        .when(proxy).bloquearHabitacion(1L);

                mockMvc.perform(post("/rooms/1/bloquear"))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrlPattern(
                                "/habitaciones/disponibles?fechaEntrada=*&fechaSalida=*"
                        ));

                verify(proxy).bloquearHabitacion(1L);
        }
    @Test
    void showRecepcionReservas_SinSesion() throws Exception {
        mockMvc.perform(post("/recepcion"));
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/recepcion"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verifyNoInteractions(proxy);
    }

    @Test
    void showRecepcionReservas_RolIncorrecto() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/recepcion")
                        .sessionAttr("userRole", "USER")) // Forzamos un rol que no es RECEPTIONIST
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/menu"));

        verifyNoInteractions(proxy);
    }

    @Test
    void showRecepcionReservas_RecepcionistaExito() throws Exception {
        List<RoomBookingResponse> mockRooms = List.of(new RoomBookingResponse(1L, 1L, "Paco Gerte", 1L, "101", LocalDate.now(), LocalDate.now().plusDays(2), RoomBookingStatus.PENDIENTE, 100.0, LocalDateTime.now()));
        List<CourtBookingResponse> mockCourts = List.of(new CourtBookingResponse(1L, 1L, "Paco Gerte", 1L, "Pista 1", LocalDate.now(), java.time.LocalTime.of(10, 0), java.time.LocalTime.of(11, 0), CourtBookingStatus.PENDIENTE, 20.0, LocalDateTime.now()));

        when(proxy.getAllRoomBookings()).thenReturn(mockRooms);
        when(proxy.getAllCourtBookings()).thenReturn(mockCourts);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/recepcion")
                        .sessionAttr("userRole", "RECEPTIONIST")
                        .sessionAttr("userId", 456L))
                .andExpect(status().isOk())
                .andExpect(view().name("user/recepcionista"))
                .andExpect(model().attribute("roomBookings", mockRooms))
                .andExpect(model().attribute("courtBookings", mockCourts))
                .andExpect(model().attributeDoesNotExist("error"));

        verify(proxy, times(1)).getAllRoomBookings();
        verify(proxy, times(1)).getAllCourtBookings();
    }

    @Test
    void showRecepcionReservas_Error() throws Exception {
        String mensajeError = "Error crítico en la base de datos de recepción";

        when(proxy.getAllRoomBookings()).thenThrow(new RuntimeException(mensajeError));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/recepcion")
                        .sessionAttr("userRole", "RECEPTIONIST"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/recepcionista"))
                .andExpect(model().attribute("error", mensajeError))
                .andExpect(model().attributeDoesNotExist("roomBookings"))
                .andExpect(model().attributeDoesNotExist("courtBookings"));

        verify(proxy, times(1)).getAllRoomBookings();
        verify(proxy, never()).getAllCourtBookings();
    }

    @Test
    void consultarDisponibilidadPistas_SinSesion() throws Exception {
        mockMvc.perform(get("/pistas"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/pistas"))
                .andExpect(model().attributeExists("year"))
                .andExpect(model().attributeExists("month"))
                .andExpect(model().attributeExists("hoy"));

         verify(proxy).getCourtsWeeklyAvailability(anyInt(), anyInt(), isNull());
    }

    @Test
    void consultarDisponibilidad_Exito() throws Exception {
        Long bookingId = 1L;

        // 1. Preparar las respuestas simuladas del proxy
        CourtBookingResponse mockBooking = new CourtBookingResponse(1L, 1L, "Paco Gerte", 2L, "Pista 2", LocalDate.of(2026, 6, 15), java.time.LocalTime.of(10, 0), java.time.LocalTime.of(11, 0), CourtBookingStatus.PENDIENTE, 20.0, LocalDateTime.now());
        List<LocalTime> mockHoras = List.of(LocalTime.of(10, 0), LocalTime.of(11, 0));

        when(proxy.getCourtBookingById(bookingId)).thenReturn(mockBooking);
        when(proxy.getHorasDisponibles(eq(2L), any(LocalDate.class))).thenReturn(mockHoras);

        // 2. Ejecutar la petición simulando el envío de formulario (@ModelAttribute y @RequestParam)
        mockMvc.perform(post("/reservas/pista/disponibilidad")
                        .param("id", bookingId.toString())       // @RequestParam Long id
                        .param("pistaId", "2")                   // Atributos del @ModelAttribute CourtBookingRequest
                        .param("fecha", "2026-06-15"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/modificarCourtBooking"))
                // Validamos que todos los atributos esperados existan en el modelo
                .andExpect(model().attribute("booking", mockBooking))
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attribute("horasDisponibles", mockHoras))
                .andExpect(model().attributeDoesNotExist("error"));

        // 3. Verificar interacciones
        verify(proxy, times(1)).getCourtBookingById(bookingId);
        verify(proxy, times(1)).getHorasDisponibles(eq(2L), any(LocalDate.class));
    }

    @Test
    void consultarDisponibilidad_Error() throws Exception {
        when(proxy.getCourtBookingById(anyLong()))
                .thenThrow(new RuntimeException("Error al conectar con el servicio de horarios"));

        var viewResolver = new org.springframework.web.servlet.view.InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        var localMockMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .build();

        localMockMvc.perform(post("/reservas/pista/disponibilidad")
                        .param("id", "1")
                        .param("pistaId", "2")
                        .param("fecha", "2026-05-17"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/modificarCourtBooking"))
                .andExpect(model().attribute("error", "Error al conectar con el servicio de horarios"));
    }

    @Test
    void bloquearHabitacion_Exito() throws Exception {

        doNothing().when(proxy).bloquearHabitacion(1L);

        mockMvc.perform(post("/rooms/1/bloquear")
                        .param("fechaEntrada", "2026-05-20")
                        .param("fechaSalida", "2026-05-25"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/habitaciones/disponibles?fechaEntrada=2026-05-20&fechaSalida=2026-05-25"))
                .andExpect(flash().attribute(
                        "success",
                        "Habitación bloqueada correctamente"));

        verify(proxy).bloquearHabitacion(1L);
    }

    @Test
    void bloquearHabitacion_Error() throws Exception {

        doThrow(new RuntimeException())
                .when(proxy)
                .bloquearHabitacion(1L);

        mockMvc.perform(post("/rooms/1/bloquear")
                        .param("fechaEntrada", "2026-05-20")
                        .param("fechaSalida", "2026-05-25"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        "/habitaciones/disponibles?fechaEntrada=2026-05-20&fechaSalida=2026-05-25"))
                .andExpect(flash().attribute(
                        "error",
                        "Error al bloquear habitación"));

        verify(proxy).bloquearHabitacion(1L);
    }

    @Test
    void bloquearHabitacion_fechasNull() throws Exception {

        doNothing().when(proxy).bloquearHabitacion(1L);

        mockMvc.perform(post("/rooms/1/bloquear"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(
                        "/habitaciones/disponibles?fechaEntrada=*&fechaSalida=*"))
                .andExpect(flash().attribute(
                        "success",
                        "Habitación bloqueada correctamente"));

        verify(proxy).bloquearHabitacion(1L);
    }

    @Test
    void showUpdateCourtBooking_ok() throws Exception {

        CourtBookingResponse booking =
                new CourtBookingResponse(
                        1L,
                        1L,
                        "Marta",
                        2L,
                        "Pista Tenis 1",
                        LocalDate.of(2026, 5, 20),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0),
                        CourtBookingStatus.CONFIRMADA,
                        20.0,
                        LocalDateTime.now()
                );

        when(proxy.getCourtBookingById(1L))
                .thenReturn(booking);

        mockMvc.perform(get("/reservas/pista/modificar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/modificarCourtBooking"))
                .andExpect(model().attributeExists("booking"))
                .andExpect(model().attribute("booking", booking));

        verify(proxy).getCourtBookingById(1L);
    }


}
