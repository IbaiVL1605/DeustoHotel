package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.Controller;
import com.example.deusto_hotel.proxy.Proxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserTestPrueba {

    private MockMvc mockMvc;
    private Proxy proxy;

    @BeforeEach
    void setUp() {
        proxy = mock(Proxy.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new Controller(proxy)).build();
    }


// DELETE OK

    @Test
    void deleteBooking_success() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 10L);

        doNothing().when(proxy).deleteRoomBooking(1L, 10L);

        mockMvc.perform(post("/reservas/eliminar/1")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas"));
    }


// SIN USUARIO

    @Test
    void deleteBooking_noUser_redirectToLogin() throws Exception {

        mockMvc.perform(post("/reservas/eliminar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }


// ERROR EN PROXY

    @Test
    void deleteBooking_proxyError() throws Exception {

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 10L);

        doThrow(new RuntimeException("Error"))
                .when(proxy).deleteRoomBooking(1L, 10L);

        mockMvc.perform(post("/reservas/eliminar/1")
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservas"));
    }


}
