package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.Controller;
import com.example.deusto_hotel.proxy.Proxy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Controller.class)
class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Proxy proxy;


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


    }

