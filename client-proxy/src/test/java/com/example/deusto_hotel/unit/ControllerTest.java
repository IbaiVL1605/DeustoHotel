package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.model.CourtType;
import com.example.deusto_hotel.model.CourtStatus;
import java.time.LocalTime;

import com.example.deusto_hotel.controller.Controller;
import com.example.deusto_hotel.dto.*;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.proxy.Proxy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
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

}
