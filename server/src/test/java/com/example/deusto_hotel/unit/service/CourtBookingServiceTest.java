package com.example.deusto_hotel.unit.service;

import com.example.deusto_hotel.dto.CourtBookingRequest;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.mapper.CourtBookingMapper;
import com.example.deusto_hotel.model.Court;
import com.example.deusto_hotel.model.CourtBooking;
import com.example.deusto_hotel.model.CourtBookingStatus;
import com.example.deusto_hotel.model.Role;
import com.example.deusto_hotel.model.User;
import com.example.deusto_hotel.repository.CourtBookingRepository;
import com.example.deusto_hotel.repository.CourtRepository;
import com.example.deusto_hotel.repository.UserRepository;
import com.example.deusto_hotel.service.CourtBookingService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@Tag("unit")

@ExtendWith(MockitoExtension.class)
public class CourtBookingServiceTest {

    @Mock
    private CourtBookingRepository courtBookingRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourtBookingMapper courtBookingMapper;

    @Mock
    private HttpSession session;

    @InjectMocks
    private CourtBookingService courtBookingService;

    @Mock
    private SimpMessagingTemplate messagingTemplate; // O la clase exacta de tu messagingTemplate
/*
    @Test
    void testFindAll() {
        CourtBooking booking = new CourtBooking();
        CourtBookingResponse response = new CourtBookingResponse(1L, 1L, "User", 1L, "Court", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), CourtBookingStatus.CONFIRMADA, 20.0, LocalDateTime.now());

        when(courtBookingRepository.findAll()).thenReturn(List.of(booking));
        when(courtBookingMapper.toResponse(booking)).thenReturn(response);

        List<CourtBookingResponse> result = courtBookingService.findAll();

        assertEquals(1, result.size());
        verify(courtBookingRepository).findAll();
        verify(courtBookingMapper).toResponse(booking);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        CourtBooking booking = new CourtBooking();
        CourtBookingResponse response = new CourtBookingResponse(1L, 1L, "User", 1L, "Court", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), CourtBookingStatus.CONFIRMADA, 20.0, LocalDateTime.now());

        when(courtBookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(courtBookingMapper.toResponse(booking)).thenReturn(response);

        CourtBookingResponse result = courtBookingService.findById(id);

        assertNotNull(result);
        verify(courtBookingRepository).findById(id);
    }

    @Test
    void testFindById_NotFound() {
        Long id = 1L;
        when(courtBookingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courtBookingService.findById(id));
    }
*/
    @Test
    void testCreate() {
        // 1. Arrange (Preparación)
        CourtBookingRequest request = new CourtBookingRequest(1L, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), 1L);

        Court court = new Court();
        court.setId(1L); // Importante para el payload del mensaje
        court.setPrecioPorHora(10.0);

        CourtBooking booking = new CourtBooking();
        booking.setPista(court);
        booking.setFecha(request.fecha());

        CourtBookingResponse response = new CourtBookingResponse(1L, 1L, "User", 1L, "Court", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), CourtBookingStatus.CONFIRMADA, 20.0, LocalDateTime.now());

        when(courtBookingMapper.toEntity(request)).thenReturn(booking);
        when(courtRepository.getReferenceById(1L)).thenReturn(court);
        when(userRepository.getReferenceById(1L)).thenReturn(new User());
        when(courtBookingRepository.save(booking)).thenReturn(booking);
        when(courtBookingMapper.toResponse(booking)).thenReturn(response);

        // 2. Act (Acción)
        CourtBookingResponse result = courtBookingService.create(request, session);

        // 3. Assert (Verificación)
        assertNotNull(result);
        assertEquals(20.0, result.precioTotal());
        verify(courtBookingRepository).save(booking);

        // Verificamos que se envió el mensaje de WebSocket
        verify(messagingTemplate).convertAndSend(
                any(String.class),
                any(Object.class)
        );
    }

    @Test
    void testUpdate() {
        Long id = 1L;
        CourtBookingRequest request = new CourtBookingRequest(1L, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), 1L);
        CourtBooking booking = new CourtBooking();
        booking.setId(id);
        Court court = new Court();
        court.setPrecioPorHora(10.0);

        when(courtBookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(courtBookingRepository.findSolapamientos(1L, request.fecha(), request.horaInicio(), request.horaFin())).thenReturn(List.of());
        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(courtBookingRepository.save(booking)).thenReturn(booking);

        CourtBookingResponse response = new CourtBookingResponse(id, 1L, "User", 1L, "Court", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(12, 0), CourtBookingStatus.CONFIRMADA, 20.0, LocalDateTime.now());
        when(courtBookingMapper.toResponse(booking)).thenReturn(response);

        CourtBookingResponse result = courtBookingService.update(id, request);

        assertNotNull(result);
        verify(courtBookingMapper).updateEntityFromRequest(request, booking);
        verify(courtBookingRepository).save(booking);
    }

    @Test
    void testDelete() {
        // 1. Arrange
        Long id = 1L;
        when(courtBookingRepository.existsById(id)).thenReturn(true);

        // 2. Act
        courtBookingService.delete(id);

        // 3. Assert
        verify(courtBookingRepository).deleteById(id);

        // Preparamos el payload esperado
        Map<String, Object> expectedPayload = Map.of("action", "DELETED", "bookingId", id);

        // CORRECCIÓN: Ambos argumentos deben usar matchers
        verify(messagingTemplate).convertAndSend(
                eq("/topic/court-updates"),
                eq((Object) expectedPayload) // Usamos eq() Y el cast a Object
        );
    }

    @Test
    void testDelete_NotFound() {
        Long id = 1L;
        when(courtBookingRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> courtBookingService.delete(id));
    }

    @Test
    void testFindByClienteId() {
        Long clienteId = 1L;
        CourtBooking booking = new CourtBooking();
        CourtBookingResponse response = new CourtBookingResponse(1L, clienteId, "User", 1L, "Court", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), CourtBookingStatus.CONFIRMADA, 20.0, LocalDateTime.now());

        when(courtBookingRepository.findByClienteId(clienteId)).thenReturn(List.of(booking));
        when(courtBookingMapper.toResponse(booking)).thenReturn(response);

        List<CourtBookingResponse> result = courtBookingService.findByClienteId(clienteId);

        assertEquals(1, result.size());
        verify(courtBookingRepository).findByClienteId(clienteId);
    }

    @Test
    void validarReservaSuccess() {
        Long idReserva = 99L;
        Long idRecepcionista = 7L;

        User recepcionista = new User();
        recepcionista.setId(idRecepcionista);
        recepcionista.setRol(Role.RECEPTIONIST);

        CourtBooking reservaPendiente = new CourtBooking();
        reservaPendiente.setId(idReserva);
        reservaPendiente.setEstado(CourtBookingStatus.PENDIENTE);

        when(userRepository.findById(idRecepcionista)).thenReturn(Optional.of(recepcionista));
        when(courtBookingRepository.findById(idReserva)).thenReturn(Optional.of(reservaPendiente));

        courtBookingService.validarReserva(idReserva, idRecepcionista);

        verify(courtBookingRepository).save(argThat(reserva ->
                reserva.getEstado() == CourtBookingStatus.CONFIRMADA
                        && reserva.getId().equals(idReserva)
        ));
    }

    @Test
    void validarReservaUsuarioNoAutenticado() {
        Long idReserva = 99L;

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> courtBookingService.validarReserva(idReserva, null)
        );

        assertEquals("Usuario no autenticado", ex.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(courtBookingRepository);
    }

    @Test
    void validarReservaUsuarioNoEncontrado() {
        Long idReserva = 99L;
        Long idRecepcionista = 7L;

        when(userRepository.findById(idRecepcionista)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> courtBookingService.validarReserva(idReserva, idRecepcionista)
        );

        assertEquals("Usuario no encontrado", ex.getMessage());
        verify(userRepository, times(1)).findById(idRecepcionista);
        verifyNoInteractions(courtBookingRepository);
    }

    @Test
    void validarReservaUsuarioNoAutorizado() {
        Long idReserva = 99L;
        Long idRecepcionista = 7L;

        User usuarioNoAutorizado = new User();
        usuarioNoAutorizado.setId(idRecepcionista);
        usuarioNoAutorizado.setRol(Role.CLIENT);

        when(userRepository.findById(idRecepcionista)).thenReturn(Optional.of(usuarioNoAutorizado));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> courtBookingService.validarReserva(idReserva, idRecepcionista)
        );

        assertEquals("Usuario no autorizado", ex.getMessage());
        verify(userRepository, times(1)).findById(idRecepcionista);
        verifyNoInteractions(courtBookingRepository);
    }

    @Test
    void validarReservaNoEncontrada() {
        Long idReserva = 99L;
        Long idRecepcionista = 7L;

        User recepcionista = new User();
        recepcionista.setId(idRecepcionista);
        recepcionista.setRol(Role.RECEPTIONIST);

        when(userRepository.findById(idRecepcionista)).thenReturn(Optional.of(recepcionista));
        when(courtBookingRepository.findById(idReserva)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> courtBookingService.validarReserva(idReserva, idRecepcionista)
        );

        assertEquals("Reserva no encontrada", ex.getMessage());
        verify(userRepository, times(1)).findById(idRecepcionista);
        verify(courtBookingRepository, times(1)).findById(idReserva);
    }

    @Test
    void validarReservaNoEstaPendiente() {
        Long idReserva = 99L;
        Long idRecepcionista = 7L;

        User recepcionista = new User();
        recepcionista.setId(idRecepcionista);
        recepcionista.setRol(Role.RECEPTIONIST);

        CourtBooking reservaConfirmada = new CourtBooking();
        reservaConfirmada.setId(idReserva);
        reservaConfirmada.setEstado(CourtBookingStatus.CONFIRMADA);

        when(userRepository.findById(idRecepcionista)).thenReturn(Optional.of(recepcionista));
        when(courtBookingRepository.findById(idReserva)).thenReturn(Optional.of(reservaConfirmada));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> courtBookingService.validarReserva(idReserva, idRecepcionista)
        );

        assertEquals("La reserva no está en estado pendiente", ex.getMessage());
        verify(userRepository, times(1)).findById(idRecepcionista);
        verify(courtBookingRepository, times(1)).findById(idReserva);
        verify(courtBookingRepository, never()).save(any(CourtBooking.class));
    }
/*
    @Test
    void testFindByPistaId() {
        Long pistaId = 1L;
        CourtBooking booking = new CourtBooking();
        CourtBookingResponse response = new CourtBookingResponse(1L, 1L, "User", pistaId, "Court", LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), CourtBookingStatus.CONFIRMADA, 20.0, LocalDateTime.now());

        when(courtBookingRepository.findByPistaId(pistaId)).thenReturn(List.of(booking));
        when(courtBookingMapper.toResponse(booking)).thenReturn(response);

        List<CourtBookingResponse> result = courtBookingService.findByPistaId(pistaId);

        assertEquals(1, result.size());
        verify(courtBookingRepository).findByPistaId(pistaId);
    }

    @Test
    void testIsDisponible() {
        Long pistaId = 1L;
        LocalDate fecha = LocalDate.now();
        LocalTime horaInicio = LocalTime.of(10, 0);
        LocalTime horaFin = LocalTime.of(12, 0);

        when(courtBookingRepository.findSolapamientos(pistaId, fecha, horaInicio, horaFin)).thenReturn(List.of());

        boolean result = courtBookingService.isDisponible(pistaId, fecha, horaInicio, horaFin);

        assertTrue(result);
        verify(courtBookingRepository).findSolapamientos(pistaId, fecha, horaInicio, horaFin);
    }

 */
    @Test
    void testCancelBookingAdmin() {
        Long id = 1L;
        CourtBooking booking = new CourtBooking();
        booking.setId(id);
        booking.setEstado(CourtBookingStatus.CONFIRMADA);

        when(courtBookingRepository.findById(id)).thenReturn(Optional.of(booking));
        when(courtBookingRepository.save(any(CourtBooking.class))).thenReturn(booking);

        courtBookingService.cancelBookingAdmin(id);

        assertEquals(CourtBookingStatus.CANCELADA, booking.getEstado());
        verify(courtBookingRepository).save(booking);

        Map<String, Object> expectedPayload = Map.of(
                "action", "CANCELLED",
                "bookingId", id
        );

        verify(messagingTemplate).convertAndSend(
                eq("/topic/court-updates"),
                eq((Object) expectedPayload)
        );
    }

    @Test
    void testCancelBookingAdmin_NotFound() {
        Long id = 1L;
        when(courtBookingRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> courtBookingService.cancelBookingAdmin(id));
        assertEquals("Reserva no encontrada", ex.getMessage());

        verify(courtBookingRepository, never()).save(any());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void findAll_exito() throws Exception{
        CourtBooking booking = mock(CourtBooking.class);

        CourtBookingResponse response = new CourtBookingResponse(
                1L,
                1L,
                "Marta",
                2L,
                "Pista 1",
                LocalDate.now(),
                LocalTime.of(10,0),
                LocalTime.of(11,0),
                CourtBookingStatus.CONFIRMADA,
                20.0,
                LocalDateTime.now()
        );

        when(courtBookingRepository.findAll())
                .thenReturn(List.of(booking));

        when(courtBookingMapper.toResponse(booking))
                .thenReturn(response);

        List<CourtBookingResponse> result = courtBookingService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(courtBookingRepository).findAll();
        verify(courtBookingMapper).toResponse(booking);
    }

    @Test
    void findAll_error() throws Exception{
        when(courtBookingRepository.findAll())
                .thenReturn(List.of());

        List<CourtBookingResponse> result = courtBookingService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(courtBookingRepository).findAll();

    }

    @Test
    void findById_exito() throws Exception{
        User cliente = mock(User.class);
        when(cliente.getId()).thenReturn(1L);
        when(cliente.getNombre()).thenReturn("Marta");

        Court pista = mock(Court.class);
        when(pista.getId()).thenReturn(2L);
        when(pista.getNombre()).thenReturn("Pista 1");

        CourtBooking booking = mock(CourtBooking.class);

        when(booking.getId()).thenReturn(1L);
        when(booking.getCliente()).thenReturn(cliente);
        when(booking.getPista()).thenReturn(pista);
        when(booking.getFecha()).thenReturn(LocalDate.now());
        when(booking.getHoraInicio()).thenReturn(LocalTime.of(10,0));
        when(booking.getHoraFin()).thenReturn(LocalTime.of(11,0));
        when(booking.getEstado()).thenReturn(CourtBookingStatus.CONFIRMADA);
        when(booking.getPrecioTotal()).thenReturn(20.0);
        when(booking.getCreadaEn()).thenReturn(LocalDateTime.now());

        when(courtBookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        CourtBookingResponse result = courtBookingService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Marta", result.clienteNombre());

        verify(courtBookingRepository).findById(1L);
    }

    @Test
    void findById_error() throws Exception{
        when(courtBookingRepository.findById(1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> courtBookingService.findById(1L));

        assertEquals("Reserva no encontrada", exception.getMessage());

        verify(courtBookingRepository).findById(1L);
    }
}
