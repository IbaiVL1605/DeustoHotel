package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.Controller;
import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.proxy.Proxy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Proxy proxy;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());


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
                        LocalDate.now().plusDays(2)
                )
        );

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
                        LocalDate.now().plusDays(2)
                )
        );

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
    }

