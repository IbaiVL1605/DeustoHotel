package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.Controller;
import com.example.deusto_hotel.proxy.Proxy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
}