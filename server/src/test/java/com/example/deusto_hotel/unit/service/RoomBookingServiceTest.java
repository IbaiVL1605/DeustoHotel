package com.example.deusto_hotel.unit.service;

import com.example.deusto_hotel.dto.RoomBookingRequest;
import com.example.deusto_hotel.model.Room;
import com.example.deusto_hotel.model.RoomBooking;
import com.example.deusto_hotel.model.RoomType;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.RoomBookingRepository;
import com.example.deusto_hotel.repository.RoomRepository;
import com.example.deusto_hotel.repository.UserRepository;
import com.example.deusto_hotel.service.RoomBookingService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@Tag("unit")

@ExtendWith(MockitoExtension.class)
public class RoomBookingServiceTest {


    @Mock
    private RoomBookingRepository roomBookingRepository;

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;

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


    // --- 1. TEST: CLIENTE NO ENCONTRADO ---
    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        // GIVEN: Un request con id_cliente = 1L
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.INDIVIDUAL, 1L, 1, null,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)
        );

        // Simulamos que el repo devuelve vacío
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(IllegalArgumentException.class, () -> roomBookingService.create(List.of(request)));

        // EXPLICACIÓN: Verificamos que NO se llamó a los otros repositorios.
        // Si el cliente no existe, el método debe morir ahí por eficiencia y seguridad.
        verifyNoInteractions(roomRepository);
        verifyNoInteractions(roomBookingRepository);
    }

    // --- 2. TEST: ÉXITO RESERVA SUITE ---
    @Test
    void shouldCreateSuiteBookingSuccessfully() {
        // GIVEN
        User cliente = new User();
        Room suite = new Room();
        suite.setTipo(RoomType.SUITE);
        suite.setPrecioPorNoche(200);

        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.SUITE, 1L, 1, 101L,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(roomRepository.findByIdAndTipo(101L, RoomType.SUITE)).thenReturn(Optional.of(suite));

        // WHEN
        roomBookingService.create(List.of(request));

        // THEN
        // EXPLICACIÓN: Verificamos que se llamó al .save().
        // Aunque el 'save' esté en un método privado, es la única forma de confirmar
        // que 'create' llegó al final del camino para una SUITE.
        verify(roomBookingRepository).save(any(RoomBooking.class));
    }

    // --- 3. TEST: ERROR FECHAS ---
    @Test
    void shouldThrowExceptionWhenCheckOutIsBeforeCheckIn() {
        // GIVEN: Fecha salida (hoy) es antes que entrada (mañana)
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.INDIVIDUAL, 1L, 1, null,
                LocalDate.now().plusDays(1), LocalDate.now()
        );

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                roomBookingService.create(List.of(request))
        );

        assertEquals("La fecha de salida debe de ser posterior a la fecha de entrada.", ex.getMessage());

        // EXPLICACIÓN: Como la validación de fechas es lo PRIMERO en el bucle,
        // no debería llamarse ni siquiera al userRepository.
        verifyNoInteractions(userRepository);
    }

    // --- 4. TEST: ÉXITO RESERVA SIMPLE (INDIVIDUAL) ---
    @Test
    void shouldCreateSimpleBookingSuccessfully() {
        // GIVEN
        User cliente = new User();
        Room habitacion = new Room();
        habitacion.setTipo(RoomType.INDIVIDUAL);

        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.INDIVIDUAL, 1L, 1, null,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(roomRepository.findRoomDisponibles(any(), any())).thenReturn(List.of(habitacion));

        // WHEN
        roomBookingService.create(List.of(request));

        // THEN
        // Verificamos que se llamó al save una vez (cantidad = 1)
        verify(roomBookingRepository, times(1)).save(any(RoomBooking.class));
    }

    @Test
    void shouldThrowExceptionWhenCheckInIsBeforeNow() {
        // GIVEN: Entrada ayer, salida mañana
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.INDIVIDUAL, 1L, 1, 101L,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)
        );

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                roomBookingService.create(List.of(request))
        );

        assertEquals("La fecha de entrada no puede ser anterior a la fecha actual.", ex.getMessage());
        verifyNoInteractions(userRepository); // Eficiencia: falla antes de buscar al usuario
    }

    @Test
    void shouldThrowExceptionWhenSuiteNotFound() {
        // GIVEN
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.SUITE, 1L, 1, 999L,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        // El repositorio devuelve vacío para esa ID de suite
        when(roomRepository.findByIdAndTipo(999L, RoomType.SUITE)).thenReturn(Optional.empty());

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                roomBookingService.create(List.of(request))
        );

        assertEquals("Habitacion no encontrada para tipo SUITE", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNoRoomsAvailableAtAll() {
        // GIVEN
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.INDIVIDUAL, 1L, 1, null,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        // Lista totalmente vacía
        when(roomRepository.findRoomDisponibles(any(), any())).thenReturn(Collections.emptyList());

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                roomBookingService.create(List.of(request))
        );

        assertEquals("No hay habitaciones disponibles para las fechas seleccionadas", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughRoomsOfSpecificType() {
        // GIVEN: Pide 3 habitaciones DOBLES
        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.DOBLE, 1L, 3, null,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)
        );

        // Solo hay 2 habitaciones en total en el sistema
        Room r1 = new Room(); r1.setTipo(RoomType.DOBLE);
        Room r2 = new Room(); r2.setTipo(RoomType.DOBLE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(roomRepository.findRoomDisponibles(any(), any())).thenReturn(List.of(r1, r2));

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                roomBookingService.create(List.of(request))
        );

        assertTrue(ex.getMessage().contains("No hay habitaciones disponibles para las fechas seleccionadas con el tipo: DOBLE"));
    }

    @Test
    void shouldThrowExceptionWhenRoomTypeIsNull() {
        // GIVEN: Tipo null y cliente válido para llegar al switch
        RoomBookingRequest request = new RoomBookingRequest(
                null, 1L, 1, 101L,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)
        );

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                roomBookingService.create(List.of(request))
        );

        assertEquals("Tipo de habitación no válido", ex.getMessage());
    }

    // --- TEST: SOLAPAMIENTO EN SUITE ---
    @Test
    void shouldThrowExceptionWhenSuiteBookingOverlaps() {
        // GIVEN: Una reserva SUITE que se solapa con otra existente
        User cliente = new User();
        cliente.setId(1L);

        Room suite = new Room();
        suite.setId(101L);
        suite.setTipo(RoomType.SUITE);
        suite.setPrecioPorNoche(200);

        // Fechas de la nueva reserva
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        RoomBookingRequest request = new RoomBookingRequest(
                RoomType.SUITE, 1L, 1, 101L,
                checkIn, checkOut
        );

        // Simulamos una reserva existente que se solapa
        RoomBooking existingReservation = new RoomBooking();
        existingReservation.setId(5L);
        existingReservation.setHabitacion(suite);
        existingReservation.setCheckIn(LocalDate.now().plusDays(2));
        existingReservation.setCheckOut(LocalDate.now().plusDays(4));

        when(userRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(roomRepository.findByIdAndTipo(101L, RoomType.SUITE)).thenReturn(Optional.of(suite));
        // El repositorio devuelve un solapamiento
        when(roomBookingRepository.findSolapamientos(101L, checkIn, checkOut))
                .thenReturn(List.of(existingReservation));

        // WHEN & THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                roomBookingService.create(List.of(request))
        );

        assertEquals("La habitación ya está reservada para las fechas seleccionadas", ex.getMessage());

        // EXPLICACIÓN: Verificamos que NO se llamó a save porque fue rechazada por solapamiento
        verify(roomBookingRepository, never()).save(any(RoomBooking.class));
    }




}
