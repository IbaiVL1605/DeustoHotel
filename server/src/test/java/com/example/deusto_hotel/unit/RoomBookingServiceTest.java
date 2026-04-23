package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.model.RoomBooking;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.RoomBookingRepository;
import com.example.deusto_hotel.service.RoomBookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomBookingServiceTest {


    @Mock
    private RoomBookingRepository roomBookingRepository;

    @InjectMocks
    private RoomBookingService roomBookingService;


// DELETE OK

    @Test
    void shouldDeleteBooking() {

        User owner = new User();
        owner.setId(10L);

        RoomBooking booking = new RoomBooking();
        booking.setId(1L);
        booking.setCliente(owner);

        when(roomBookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        roomBookingService.delete(1L, 10L);

        verify(roomBookingRepository).deleteById(1L);
    }


// DELETE - NOT FOUND

    @Test
    void shouldThrowIfDeleteNonExistingBooking() {

        when(roomBookingRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                roomBookingService.delete(1L, 10L)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(roomBookingRepository, never()).deleteById(any());
    }


// DELETE - UNAUTHORIZED

    @Test
    void shouldThrowIfDeleteWithoutAuthenticatedUser() {

        User owner = new User();
        owner.setId(10L);

        RoomBooking booking = new RoomBooking();
        booking.setId(1L);
        booking.setCliente(owner);

        when(roomBookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                roomBookingService.delete(1L, null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        verify(roomBookingRepository, never()).deleteById(any());
    }


// DELETE  FORBIDDEN

    @Test
    void shouldThrowIfDeleteByNonOwnerUser() {

        User owner = new User();
        owner.setId(10L);

        RoomBooking booking = new RoomBooking();
        booking.setId(1L);
        booking.setCliente(owner);

        when(roomBookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                roomBookingService.delete(1L, 99L)
        );

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(roomBookingRepository, never()).deleteById(any());
    }


}
