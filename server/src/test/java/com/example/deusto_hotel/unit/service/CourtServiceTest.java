package com.example.deusto_hotel.unit.service;

import com.example.deusto_hotel.dto.CourtAvailabilityDTO;
import com.example.deusto_hotel.dto.CourtBookingResponse;
import com.example.deusto_hotel.dto.CourtResponse;
import com.example.deusto_hotel.dto.WeekAvailability;
import com.example.deusto_hotel.model.*;
import com.example.deusto_hotel.repository.CourtBookingRepository;
import com.example.deusto_hotel.repository.CourtRepository;
import com.example.deusto_hotel.service.CourtService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Tag("unit")

@ExtendWith(MockitoExtension.class)
public class CourtServiceTest {

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private CourtBookingRepository courtBookingRepository;

    @InjectMocks
    private CourtService courtService;

    // ===================================================================================
    // TESTS PARA: findAvailableByDate
    // ===================================================================================

    @Test
    public void testFindAvailableByDate_FechaInvalida() {
        // Al pasar una fecha que no se puede parsear, debe devolver una lista vacía y
        // no lanzar excepción
        List<CourtAvailabilityDTO> result = courtService.findAvailableByDate("fecha-falsa");
        assertTrue(result.isEmpty());
        verify(courtRepository, never()).findByEstado(any());
    }

    @Test
    public void testFindAvailableByDate_Exito() {
        String fechaStr = "2026-10-10";
        LocalDate targetDate = LocalDate.parse(fechaStr);

        // Crear un mock de pista
        Court mockPista = mock(Court.class);
        when(mockPista.getId()).thenReturn(1L);
        when(mockPista.getNombre()).thenReturn("Pista Central");
        when(mockPista.getTipo()).thenReturn(CourtType.PADEL);

        // Crear mock del cliente y la reserva
        var mockCliente = mock(com.example.deusto_hotel.model.User.class);
        when(mockCliente.getId()).thenReturn(100L);
        when(mockCliente.getNombre()).thenReturn("Pedro");

        var mockReserva = mock(com.example.deusto_hotel.model.CourtBooking.class);
        when(mockReserva.getId()).thenReturn(10L);
        when(mockReserva.getFecha()).thenReturn(targetDate);
        when(mockReserva.getEstado()).thenReturn(CourtBookingStatus.CONFIRMADA);
        when(mockReserva.getHoraInicio()).thenReturn(LocalTime.of(10, 0));
        when(mockReserva.getHoraFin()).thenReturn(LocalTime.of(11, 0));
        when(mockReserva.getPrecioTotal()).thenReturn(15.50);
        when(mockReserva.getCreadaEn()).thenReturn(LocalDateTime.now());
        when(mockReserva.getCliente()).thenReturn(mockCliente);
        when(mockReserva.getPista()).thenReturn(mockPista);

        when(mockPista.getCourtBookings()).thenReturn(List.of(mockReserva));
        when(courtRepository.findByEstado(CourtStatus.DISPONIBLE)).thenReturn(List.of(mockPista));

        // Ejecutar test
        List<CourtAvailabilityDTO> result = courtService.findAvailableByDate(fechaStr);

        // Verificaciones
        assertFalse(result.isEmpty());

        CourtAvailabilityDTO padelGroup = result.stream()
                .filter(dto -> dto.tipo() == CourtType.PADEL)
                .findFirst()
                .orElse(null);

        assertNotNull(padelGroup);
        assertEquals(1, padelGroup.reservas().size());

        assertEquals("Pedro", padelGroup.reservas().get(0).clienteNombre());
        assertEquals(10L, padelGroup.reservas().get(0).id());
    }

    // ===================================================================================
    // TESTS PARA: findAvailableByTypeAndWeek
    // ===================================================================================

    @Test
    public void testFindAvailableByTypeAndWeek_TipoInvalido() {
        // Al fallar el parseo de tipo, hace fallback a buscar TODAS las disponibles
        courtService.findAvailableByTypeAndWeek("NO_EXISTE", 1);
        verify(courtRepository).findByEstado(CourtStatus.DISPONIBLE);
        verify(courtRepository, never()).findByTipoAndEstado(any(), any());
    }

    @Test
    public void testFindAvailableByTypeAndWeek_TipoValido() {
        // Cuando especificamos tipo válido
        when(courtRepository.findByTipoAndEstado(CourtType.TENIS, CourtStatus.DISPONIBLE))
                .thenReturn(new ArrayList<>());

        courtService.findAvailableByTypeAndWeek("TENIS", 2);

        verify(courtRepository).findByTipoAndEstado(CourtType.TENIS, CourtStatus.DISPONIBLE);
    }

    @Test
    public void testFindAvailableByTypeAndWeek_SinSemana() {
        // Si la semana es null, debe calcular la semana actual sin petar
        when(courtRepository.findByTipoAndEstado(CourtType.PADEL, CourtStatus.DISPONIBLE))
                .thenReturn(new ArrayList<>());

        courtService.findAvailableByTypeAndWeek("PADEL", null);

        verify(courtRepository).findByTipoAndEstado(CourtType.PADEL, CourtStatus.DISPONIBLE);
    }

    @Test
    void testFindAvailableByTypeAndWeek_filtradoReservas() {

        CourtBooking bookingValido = mock(CourtBooking.class);
        when(bookingValido.getFecha()).thenReturn(LocalDate.now().plusDays(1));
        when(bookingValido.getEstado()).thenReturn(CourtBookingStatus.CONFIRMADA);
        when(bookingValido.getId()).thenReturn(1L);

        CourtBooking bookingFuera = mock(CourtBooking.class);
        when(bookingFuera.getFecha()).thenReturn(LocalDate.now().plusDays(100));

        CourtBooking bookingCancelado = mock(CourtBooking.class);
        when(bookingCancelado.getFecha()).thenReturn(LocalDate.now().plusDays(1));
        when(bookingCancelado.getEstado()).thenReturn(CourtBookingStatus.CANCELADA);

        User cliente = mock(User.class);
        when(cliente.getId()).thenReturn(1L);
        when(cliente.getNombre()).thenReturn("Test");

        Court pista = mock(Court.class);
        when(pista.getId()).thenReturn(1L);
        when(pista.getNombre()).thenReturn("Pista 1");

        Court court = mock(Court.class);
        when(court.getTipo()).thenReturn(CourtType.TENIS);
        when(court.getTipo()).thenReturn(CourtType.TENIS);
        when(court.getCourtBookings())
                .thenReturn(List.of(bookingValido, bookingFuera, bookingCancelado));

        when(bookingValido.getCliente()).thenReturn(cliente);
        when(bookingValido.getPista()).thenReturn(pista);
        when(bookingValido.getHoraInicio()).thenReturn(LocalTime.of(10, 0));
        when(bookingValido.getHoraFin()).thenReturn(LocalTime.of(11, 0));
        when(bookingValido.getPrecioTotal()).thenReturn(20.0);
        when(bookingValido.getCreadaEn()).thenReturn(LocalDateTime.now());

        when(courtRepository.findByTipoAndEstado(any(), any()))
                .thenReturn(List.of(court));

        List<CourtAvailabilityDTO> result = courtService.findAvailableByTypeAndWeek("TENIS", 1);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        List<CourtBookingResponse> bookings = result.get(0).reservas();

        assertEquals(1, bookings.size());
        assertEquals(1L, bookings.get(0).id());
    }

    // ===================================================================================
    // TESTS PARA: findWeeklyAvailability
    // ===================================================================================

    @Test
    public void testFindWeeklyAvailability_SinTipo() {
        Court mockPista = mock(Court.class);
        when(mockPista.getId()).thenReturn(1L);
        when(mockPista.getNombre()).thenReturn("Pista Generica");
        when(mockPista.getTipo()).thenReturn(CourtType.PADEL);
        when(mockPista.getPrecioPorHora()).thenReturn(10.0);
        when(mockPista.getEstado()).thenReturn(CourtStatus.DISPONIBLE);

        when(courtRepository.findByEstado(CourtStatus.DISPONIBLE)).thenReturn(List.of(mockPista));

        when(courtBookingRepository.findSolapamientos(anyLong(), any(LocalDate.class), any(LocalTime.class),
                any(LocalTime.class)))
                .thenReturn(Collections.emptyList());

        List<WeekAvailability> result = courtService.findWeeklyAvailability(2026, 2, null);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertFalse(result.getFirst().days().getFirst().slots().isEmpty());
        assertEquals(14, result.getFirst().days().getFirst().slots().size());

        verify(courtRepository).findByEstado(CourtStatus.DISPONIBLE);
    }

    @Test
    public void testFindWeeklyAvailability_ConTipoYReservado() {
        Court mockPista = mock(Court.class);
        when(mockPista.getId()).thenReturn(5L);

        when(courtRepository.findByTipoAndEstado(CourtType.TENIS, CourtStatus.DISPONIBLE))
                .thenReturn(List.of(mockPista));

        var mockSolapamiento = mock(com.example.deusto_hotel.model.CourtBooking.class);
        when(courtBookingRepository.findSolapamientos(eq(5L), any(LocalDate.class), any(LocalTime.class),
                any(LocalTime.class)))
                .thenReturn(List.of(mockSolapamiento));

        List<WeekAvailability> result = courtService.findWeeklyAvailability(2026, 3, CourtType.TENIS);

        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertTrue(result.getFirst().days().getFirst().slots().isEmpty());

        verify(courtRepository).findByTipoAndEstado(CourtType.TENIS, CourtStatus.DISPONIBLE);
    }

    @Test
    void blockCourt_Success() {
        Court court = new Court();
        court.setId(1L);
        court.setNombre("Pista 1");
        court.setEstado(CourtStatus.DISPONIBLE);

        when(courtRepository.findById(1L)).thenReturn(Optional.of(court));
        when(courtRepository.save(any(Court.class))).thenReturn(court);

        CourtResponse response = courtService.blockCourt(1L);

        assertEquals(CourtStatus.BLOQUEADA, response.estado());
        verify(courtRepository).save(court);
    }

}