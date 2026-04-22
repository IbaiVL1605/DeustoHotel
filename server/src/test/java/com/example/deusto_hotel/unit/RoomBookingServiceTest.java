package com.example.deusto_hotel.unit;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.dto.RoomBookingResponse;
import com.example.deusto_hotel.mapper.RoomBookingMapper;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomBooking;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.RoomBookingRepository;
import com.example.deusto_hotel.repository.RoomRepository;
import com.example.deusto_hotel.repository.UserRepository;
import com.example.deusto_hotel.service.RoomBookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomBookingServiceTest {

    @Mock
    private RoomBookingRepository roomBookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomBookingMapper roomBookingMapper;

    @InjectMocks
    private RoomBookingService roomBookingService;

    // CREATE OK
    @Test
    void shouldCreateBookingSuccessfully() {

        RoomBookingRequest request = new RoomBookingRequest(
                1L,
                LocalDate.of(2026, 3, 25),
                LocalDate.of(2026, 3, 27),
                10L
        );

        Room room = new Room(1L, RoomType.INDIVIDUAL);
        room.setPrecioPorNoche(100);

        User user = new User();
        user.setId(10L);

        RoomBooking booking = new RoomBooking();

        RoomBookingResponse response = mock(RoomBookingResponse.class);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(roomBookingMapper.toEntity(request)).thenReturn(booking);
        when(roomBookingMapper.toResponse(any())).thenReturn(response);

        RoomBookingResponse result = roomBookingService.create(request);

        assertNotNull(result);
        verify(roomBookingRepository).save(any(RoomBooking.class));
    }

    // CREATE - habitación no existe
    @Test
    void shouldThrowIfRoomNotFound() {

        RoomBookingRequest request = new RoomBookingRequest(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                10L
        );

        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                roomBookingService.create(request)
        );

        verify(roomBookingRepository, never()).save(any());
    }

    // CREATE - usuario no existe
    @Test
    void shouldThrowIfUserNotFound() {

        RoomBookingRequest request = new RoomBookingRequest(
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                10L
        );

        Room room = new Room(1L, RoomType.INDIVIDUAL);
        room.setId(1L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                roomBookingService.create(request)
        );

        verify(roomBookingRepository, never()).save(any());
    }

    // FIND BY ID
    @Test
    void shouldFindBookingById() {

        RoomBooking booking = new RoomBooking();
        booking.setId(1L);

        RoomBookingResponse response = mock(RoomBookingResponse.class);

        when(roomBookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(roomBookingMapper.toResponse(booking)).thenReturn(response);

        RoomBookingResponse result = roomBookingService.findById(1L);

        assertNotNull(result);
    }

    // FIND BY ID - no existe
    @Test
    void shouldThrowIfBookingNotFound() {

        when(roomBookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                roomBookingService.findById(1L)
        );
    }

    // DELETE OK
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

    // DELETE - no existe
    @Test
    void shouldThrowIfDeleteNonExistingBooking() {

        when(roomBookingRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                roomBookingService.delete(1L, 10L)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        verify(roomBookingRepository, never()).deleteById(any());
    }

    // DELETE - sin usuario en sesión
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

    // DELETE - usuario distinto al dueño
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

    // FIND BY CLIENTE
    @Test
    void shouldFindByClienteId() {

        RoomBooking booking = new RoomBooking();
        RoomBookingResponse response = mock(RoomBookingResponse.class);

        when(roomBookingRepository.findByClienteId(10L)).thenReturn(List.of(booking));
        when(roomBookingMapper.toResponse(booking)).thenReturn(response);

        List<RoomBookingResponse> result = roomBookingService.findByClienteId(10L);

        assertEquals(1, result.size());
    }

    // FIND BY HABITACION
    @Test
    void shouldFindByHabitacionId() {

        RoomBooking booking = new RoomBooking();
        RoomBookingResponse response = mock(RoomBookingResponse.class);

        when(roomBookingRepository.findByHabitacionId(1L)).thenReturn(List.of(booking));
        when(roomBookingMapper.toResponse(booking)).thenReturn(response);

        List<RoomBookingResponse> result = roomBookingService.findByHabitacionId(1L);

        assertEquals(1, result.size());
    }
}