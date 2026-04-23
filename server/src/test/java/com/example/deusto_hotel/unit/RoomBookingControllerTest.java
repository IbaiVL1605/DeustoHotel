package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.controller.RoomBookingController;
import com.example.deusto_hotel.service.RoomBookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomBookingController.class)
class RoomBookingControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomBookingService roomBookingService;


// DELETE OK

    @Test
    void delete_success() throws Exception {

        doNothing().when(roomBookingService).delete(1L, 10L);

        mockMvc.perform(delete("/api/v1/room-bookings/1")
                        .param("userId", "10"))
                .andExpect(status().isNoContent());
    }


// DELETE NOT FOUND

    @Test
    void delete_notFound() throws Exception {

        doThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Reserva no encontrada"))
                .when(roomBookingService).delete(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/room-bookings/1")
                        .param("userId", "10"))
                .andExpect(status().isBadRequest());
    }


// DELETE UNAUTHORIZED

    @Test
    void delete_unauthorized_whenNoUser() throws Exception {

        mockMvc.perform(delete("/api/v1/room-bookings/1"))
                .andExpect(status().isBadRequest());
    }


}
